package com.rho.scrap

object CoppelParsing {

  import scala.util.matching.Regex
  import scala.math.round
  import com.rho.scrap.CoppelCase.{Department,Category,Item}

  def strip(body: String): String = "\\r|\\n|\\t".r replaceAllIn(body," ")
  def stripSpaces(s: String) = "\\s".r replaceAllIn(s,"")
  def strip4CSV(s: String) = "\\|".r replaceAllIn(s," ")
  def stripNaN(s: String) = "[$,nbps;&\\s]".r replaceAllIn(s,"")
  def toCents(s: String):Int = round(round((s.toDouble*100.0)))
  def stripND(s: String): String = {"\\D".r replaceAllIn(s,"")}



  def readDepartments(body: String): List[Department] = {
    val regex_name = new Regex(
      "<a\\s+?onmouseover.*?"+
      "departmentMenu_(\\d{1,}?)\\D.*?"+
      "href.*?www.coppel.com(.+?)(\"|').+?"+
      "<span>(.+?)</span>")
    (for (entry<-(regex_name findAllMatchIn body).toList)
      yield {Department(entry.group(1),entry.group(2),entry.group(4))})
  }



  def readCategories(body: String, parentId: String): List[Category] = {
    val regex = new Regex(
      "<a\\s+?id=.categoryLink_"+parentId+"_(\\d+?)\\D.+?"+
      "coppel.com(.+?)(\"|').*?>"+
      ".*?([^\\s].+?)\\s*?</a>"
    )
    def noAmp(s: String): String = "amp;".r replaceAllIn(s,"")
    (for (entry<-(regex findAllMatchIn body).toList) yield {
      Category(entry.group(1),noAmp(entry.group(2)),entry.group(4),parentId)
    })
  }



  def readItems(body: String, parent: String): List[Item] = {
    def getTitles: List[List[String]] = {
      val regex = new Regex(
        "<div\\s+?class=.product_title.>.+?"+
        "id=.product_name_(\\d+?)\\D.+?"+
        "http://www.coppel.com(.+?)(\"|').+?"+
        "<h2>(.+?)</h2>"
      )
      (for (entry<-(regex findAllMatchIn body).toList) yield {
          List(entry.group(1),entry.group(2),entry.group(4))
      })
    }
    def listProd(list: List[String]): Item = {
      Item(stripNaN(list(0)),
        strip4CSV(list(1)),
        strip4CSV(list(2)),
        parent,
        toCents(stripNaN(list(3))),
        toCents(stripNaN(list(4))),
        stripND(list(5)).toInt,
        toCents(stripNaN(list(6))))
    }
    getTitles.map{base => 
      listProd(base++getRegularPrice(base.head,body)++getCreditPrice(base.head,body))
    }
  }



  def readItemData(product: Item, body: String): Item = {
    def listProd(list: List[String]): Item = {
      Item(product.id,
        product.name,
        product.path,
        product.parent,
        toCents(stripNaN(list(0))),
        toCents(stripNaN(list(1))),
        stripND(list(2)).toInt,
        toCents(stripNaN(list(3))))
    }
    listProd(getRegularPrice(product.id,body)++getCreditPrice(product.id,body))
  }



  private def getRegularPrice(id: String, body: String): List[String] = {
    val regex = new Regex("offerPrice_"+id+"[^>]+?>\\s*?(\\$[^>]+?)</span>")
    (regex findFirstMatchIn body) match {
      case Some(x) => List(x.group(1))
      case None => throw new Error("No match for regular price data")
    }
  }
  private def getCreditPrice(id: String, body: String): List[String] = {
    val regex = new Regex(
      "<dt>([^e]+?)en([^q]+?)quincenas[^<]*?"+
      "<span\\s+?id=.twoWeeksprice_"+id+".+?"+ 
      "<span.+?id=.creditCoppelPrice_"+id+"[^>]+?>([^Q]+?)Quincenal</span>"
    )
    (regex findFirstMatchIn body) match {
      case Some(x) => List(x.group(1),x.group(2),x.group(3))
      case None => List("0","0","0")
    }
  }



}
