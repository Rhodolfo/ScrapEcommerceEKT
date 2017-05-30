package com.rho.scrap

object CoppelHTTP {

  import com.rho.client.RhoClient
  import com.rho.scrap.CoppelParsing._

  val client = new RhoClient(Scheme="http",Host="www.coppel.com")
  val prefix = "[HTTP] "

  def getTree: (List[Department],Map[String,List[Category]]) = {
    val body = strip(client.doGET(Map(),""))
    val deps = readDepartments(body)
    val cats = deps.map(dep => (dep.id,readCategories(body,dep.id))).toMap
    (deps,cats)
  }

  def getCategoryProducts(categoryId: String, index: Int = 0): List[Product] {
    Nil
  }

}
