package com.rho.scrap

object WalmartHTTP {

  import com.rho.client.RhoClient
  import com.rho.scrap.WalmartClasses.{Menu,Department,Product}
  import com.rho.scrap.WalmartParse.{parseMenu,parseProduct}

  val host = "www.walmart.com.mx"
  val client = new RhoClient(Scheme="https",Host=host)
  val depsPath = "/app/webPart/taxonomy/menumg.js"
  val prodPath = "/WebControls/hlGetProductsByLine.ashx"

  def rawGET(path: String, params: Map[String,String] = Map()): String = client.doGET(params,path)

  def getMenu: List[Department] = parseMenu(client.doGET(Map(),depsPath))
  
  private def prodMap(par:String): Map[String,String] = Map("linea"->par)

  def getProducts(par: String): List[Product] = parseProduct(client.doGET(prodMap(par),prodPath))

}
