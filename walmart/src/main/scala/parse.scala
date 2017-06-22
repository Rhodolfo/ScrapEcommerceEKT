package com.rho.scrap

object WalmartParse {

  import com.rho.scrap.WalmartClasses.{Menu,Department,Product}
  import com.rho.file.quickFunc.writeToFile
  import net.liftweb.json._
  import scala.util.matching.Regex
  implicit val formats = DefaultFormats

  def parseMenu(body: String): List[Department] = {
    val jlist = (parse(body) \\ "MenuPrincipal").children
    val menus = for {entry <- jlist} yield {entry.extract[Menu]}
    if (menus.size==1) {
      menus.head.Elements.filter(_.url != null)
    } else throw new Error("Parse error for department menu")
  }

  def parseProduct(body: String): List[Product] = {
    val json = parse(body).children
    for {entry <- json} yield {entry.extract[Product]}
  }

}
