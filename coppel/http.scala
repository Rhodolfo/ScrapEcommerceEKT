package com.rho.scrap

object CoppelHTTP {

  import com.rho.client.RhoClient
  import com.rho.scrap.CoppelParsing._

  val prefix = "[HTTP] "
  val catpth = "/ProductListingView"
  val client = new RhoClient(Scheme="http",Host="www.coppel.com")

  def getTree: (List[Department],Map[String,List[Category]]) = {
    System.out.println(prefix+"Fetching index page")
    val body = strip(client.doGET(Map(),""))
    System.out.println(prefix+"Done")
    val deps = readDepartments(body)
    val cats = deps.map(dep => (dep.id,readCategories(body,dep.id))).toMap
    (deps,cats)
  }

  def getCategoryProducts(categoryId: String, index: Int = 0): List[Product] = {
    val getParams = Map(
      "catalogId"->"10001",
      "categoryId"->categoryId,
      "storeId"->"12761",
      "disableProductCompare"->"false")
    val postParams = Map(
      "contentBeginIndex"->"0",
      "productBeginIndex"->index.toString,
      "beginIndex"->index.toString,
      "pageView"->"list",
      "resultType"->"products",
      "pageSize"->"72",
      "storeId"->"12761",
      "catalogId"->"10001",
      "requesttype"->"ajax")
    System.out.println(prefix+"Fetching products for category "+categoryId+" at index "+index)
    val body = strip(client.doPOST(postParams,getParams,catpth))
    System.out.println(prefix+"Done")
    com.rho.file.quickFunc.writeToFile("lastCatPage.html",body,false,"UTF-8")
    val prod = readProducts(body)
    prod
  }

}
