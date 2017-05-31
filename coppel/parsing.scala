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

  case class Product(id: String, path: String, name: String, price: Int, 
  twoWeeksPrice: Int, twoWeeksNumber: Int, twoWeeksPayment: Int) extends Page(id, path, name) {
    override def toString: String = {
      id+separator+root+path+" "+separator+name+separator+
      price+separator+twoWeeksPrice+separator+twoWeeksPayment+separator+twoWeeksNumber
    }
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
    def getData(id: String): List[String] = {
      val regex = new Regex(
        "<span.+?(offer)Price_"+id+"[^>]+?>\\s*?(\\$.+?)</span>.+?"+
        "<dt>(.+?)en(.+?)quincenas.*?<span\\s+?id=.twoWeeksprice.+?"+ 
        "<span.+?id=.creditCoppelPrice_"+id+".+?>(.+?)Quincenal</span>"
      )
      println(id)
      (regex findFirstMatchIn body) match {
        case Some(x) => List(x.group(2),x.group(3),x.group(4),x.group(5))
        case None => throw new Error("No match")
      }
    }
    def listProd(list: List[String]): Product = {
      println(list)
      Product(stripNaN(list(0)),
        strip4CSV(list(1)),
        strip4CSV(list(2)),
        toCents(stripNaN(list(3))),
        toCents(stripNaN(list(4))),
        stripND(list(5)).toInt,
        toCents(stripNaN(list(6))))
    }
    println("RegEx matching")
    val parts = getTitles
    println("RegEx matching 2")
    val lists = parts.map(part => part++getData(part.head))
    lists.foreach(println)
    println("Map to Product")
    lists.map(listProd)
  }

}