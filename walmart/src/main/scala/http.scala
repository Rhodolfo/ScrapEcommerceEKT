package com.rho.scrap

object WalmartHTTP {

  import com.rho.client.RhoClient
  import com.rho.scrap.WalmartClasses.{Menu,Department}
  import com.rho.scrap.WalmartParse.{parseMenu}

  val host = "www.walmart.com.mx"
  val client = new RhoClient(Scheme="https",Host=host)
  val departments_path = "/app/webPart/taxonomy/menumg.js"

  def getMenu: Menu = parseMenu(client.doGET(Map(),departments_path))

}
