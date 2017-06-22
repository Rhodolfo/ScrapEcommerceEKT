package com.rho.scrap

object WalmartClasses {

  

  val sep = "|"
  private def concat(a: String, b:String): String = if (a.isEmpty) b else a+sep+b

  case class Product(
    ProductUrl: String,
    upc: String,
    DepartmentId: String,
    Family: String,
    Line: String,
    Brand: String,
    Description: String,
    Precio: String,
    PrecioGranel: String,
    PrecioNumerico: Int
  ) {
    def header: String = {
      val list = List("ProductUrl","upc","DepartmentId","Family","Line","Brand",
        "Description","Precio","PrecioGranel","PrecioNumerico")
      list.foldLeft[String]("")(concat)
    }

    override def toString: String = {
      val list = List(ProductUrl,upc,DepartmentId,Family,Line,Brand,
        Description,Precio,PrecioGranel,PrecioNumerico.toString)
      list.foldLeft[String]("")(concat)
    }
  }

  case class Department(
    Elements: List[Department],
    Id: String, 
    Description: String,
    departmentName: String, 
    url: String) {

    def lines: List[Department] = {
      def iter(list: List[Department], acc: List[Department]): List[Department] = {
        if (list.isEmpty) acc
        else if (list.head.Elements.isEmpty) iter(list.tail, list.head::acc)
        else iter(list.head.Elements++list.tail, acc)
      }
      iter(List(this),Nil)
    }

    def products: List[Product] = {
      if (Elements.isEmpty) com.rho.scrap.WalmartHTTP.getProducts(departmentName)
      else throw new Error("Department must be empty to fetch product list")
    }

    override def toString = departmentName+sep+url+sep+Description

  }

  case class Menu(Elements: List[Department], MenuName: String)

}
