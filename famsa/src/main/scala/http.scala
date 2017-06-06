package com.rho.scrap

object FamsaHTTP {

  import com.rho.client.RhoClient
  import com.rho.file.quickFunc.readIntoString
  import com.rho.scrap.FamsaParsing.{readPages,readItems,readSubCategories}
  import com.rho.scrap.FamsaClasses.{Page,Item}

  private def strip(s: String) = "(\\t|\\n|\\r)".r replaceAllIn(s,"")

  val host = "www.famsa.com"
  val client = new RhoClient(Scheme="https",Host=host)
  val prefix = "[HTTP] "

  def getPages: List[Page] = {
    System.out.println(prefix+"Fetching categories")
    val body = strip(client.doGET(Map(),""))
    System.out.println(prefix+"Done")
    readPages(body)
  }

  def getItems(page: Page): List[Item] = {
    System.out.println(prefix+"Fetching items for "+page)
    val body = strip(client.doGET(Map(),page.path))
    System.out.println(prefix+"Done")
    readItems(body,page)
  }

  def getSubCategories(page: Page): List[Page] = {
    System.out.println(prefix+"Fetching subcategories for "+page)
    val body = strip(client.doGET(Map(),page.path))
    System.out.println(prefix+"Done")
    readSubCategories(body)
  }



}
