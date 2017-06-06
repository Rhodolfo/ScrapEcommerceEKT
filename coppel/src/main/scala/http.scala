package com.rho.scrap

object CoppelHTTP {

  import com.rho.client.RhoClient
  import com.rho.scrap.CoppelCase.{Department,Category,Item}
  import com.rho.scrap.CoppelParsing.{readDepartments,readCategories,readItems,readItemData,strip}

  val prefix = "[HTTP] "
  val catpth = "/ProductListingView"
  val client = new RhoClient(Scheme="http",Host="www.coppel.com")
  val tsleep = 100 // milliseconds



  def getTree: (List[Department],Map[String,List[Category]]) = {
    System.out.println(prefix+"Fetching index page")
    Thread.sleep(tsleep)
    val body = strip(client.doGET(Map(),""))
    System.out.println(prefix+"Done")
    val deps = readDepartments(body)
    val cats = deps.map(dep => (dep.id,readCategories(body,dep.id))).toMap
    (deps,cats)
  }



  def getCategoryItems(categoryId: String): List[Item] = {
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
    def iter(acc: List[Item], page: Int = 1): List[Item] = {
      val index = (page-1)*perPage
      System.out.println(prefix+"Fetching products for category "+categoryId+", page "+page)
      Thread.sleep(tsleep)
      val body = strip(client.doPOST(postParams(index),getParams,catpth))
      System.out.println(prefix+"Done")
      val prod = readItems(body, categoryId)
      if (prod.size<72) acc++prod
      else iter(acc++prod,page+1)
    }
    iter(List[Item]())
  }


  def getItemData(product: Item): Item = {
    System.out.println(prefix+"Fetching product from "+product.path)
    Thread.sleep(tsleep)
    val body = strip(client.doGET(Map(),product.path))
    System.out.println(prefix+"Done")
    readItemData(product,body)
  }

}
