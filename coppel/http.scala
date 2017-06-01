package com.rho.scrap

object CoppelHTTP {

  import com.rho.client.RhoClient
  import com.rho.scrap.CoppelParsing.{readDepartments,readCategories,readProducts,Department,Category,strip}

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

  def getCategoryProducts(categoryId: String): List[Product] = {
    val getParams = Map("catalogId"->"10001",
      "categoryId"->categoryId,
      "storeId"->"12761",
      "disableProductCompare"->"false")
    def postParams(index: Int): Map[String,String] = {
      Map("contentBeginIndex"->"0",
      "productBeginIndex"->index.toString,
      "beginIndex"->index.toString,
      "pageView"->"list",
      "resultType"->"products",
      "pageSize"->"72",
      "storeId"->"12761",
      "catalogId"->"10001",
      "requesttype"->"ajax")
    }
    val perPage = 72
    def iter(acc: List[Product], page: Int = 1): List[Product] = {
      val index = (page-1)*perPage
      System.out.println(prefix+"Fetching products for category "+categoryId+", page "+page)
      val body = strip(client.doPOST(postParams(index),getParams,catpth))
      System.out.println(prefix+"Done")
      val prod = readProducts(body)
      if (prod.size<72) acc++prod
      else iter(acc++prod,page+1)
    }
    iter(List[Product]())
  }

}
