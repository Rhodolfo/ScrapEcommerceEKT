package com.rho.scrap

object WalmartParse {

  import com.rho.scrap.WalmartClasses.{Menu,Department}
  import com.rho.file.quickFunc.writeToFile
  import net.liftweb.json._
  implicit val formats = DefaultFormats

  def parseMenu(body: String): Menu = {
    val jlist = (parse(body) \\ "MenuPrincipal").children
    val menus = for {entry <- jlist} yield {entry.extract[Menu]}
    if (menus.size==1) menus.head
    else throw new Error("Parse error for department menu")
  }

}
