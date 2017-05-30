package com.rho.scrap

object CoppelParsing {

  import scala.util.matching.Regex
  import scala.math.round
  import com.rho.scrap.CoppelHTTP.client

  val separator = "|"
  val root = "http://www.coppel.com"

  abstract class Page(id: String, path: String, name: String) {
    override def toString: String = id+separator+root+path+" "+separator+name
  }

  case class Department(id: String, path: String, name: String)  extends Page(id,path,name)

  case class Category(id: String, path: String, name: String, parent: String) extends Page(id,path,name) {
    override def toString: String = id+separator+root+path+" "+separator+name+separator+parent
  }

  case class Product(id: String, path: String, name: String, price: Int, twoWeeksPrice: Int, twoWeeksNumber: Int, twoWeeksPayment: Int) 
  extends Page(id, path, name) {
    def this(list: List[String]) = {
      this(
        stripNaN(list(0)),
        strip4CSV(list(1)),
        strip4CSV(list(2)),
        toCents(stripNaN(list(3))),
        toCents(stripNaN(list(4))),
        stripND(list(5)).toInt,
        toCents(stripNaN(list(6)))
      )
    }
    override def toString: String = {
      id+separator+root+path+" "+separator+name+separator+
      price+separator+twoWeeksPrice+separator+twoWeeksPayment+separator+twoWeeksNumber
    }
  }
  object Product {
    def apply(list: List[String]) = new Product(list)
  }


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

  def readProducts(body: String): List[Product] = {
    def getTitles: Map[String,List[String]] = {
      val regex = new Regex(
        "<div\\s+?class=.product_title.>.+?"+
        "id=.product_name_(\\d+?)\\D.+?"+
        "http://www.coppel.com(.+?)(\"|').+?"+
        "<h2>(.+?)</h2>"
      )
      (for (entry<-(regex findAllMatchIn body).toList) yield {
          (entry.group(1), List(entry.group(2),entry.group(4)))
      }).toMap
    }
    def getData: Map[String,List[String]] = {
      val regex = new Regex(
        "<div\\s+?class=.priceTable.>.+?"+
        "<span.+?id=.offerPrice_(\\d+?)\\D.+?>(.+?)</span>.+?"+
        "<dt>(.+?)en(.+?)quincenas.*?<span\\s+?id=.twoWeeksprice.+?"+ 
        "<span.+?id=.creditCoppelPrice_\\d+?\\D.+?>(.+?)Quincenal</span>"
      )
      (for (entry<-(regex findAllMatchIn body).toList) yield {
        (entry.group(1),List(entry.group(2),entry.group(3),entry.group(4),entry.group(5)))
      }).toMap
    }
    val titles = getTitles
    val data = getData
    titles.keys.map(key => key::titles(key)++data(key)).map(Product(_)).toList
  }

}
