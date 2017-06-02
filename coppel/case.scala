package com.rho.scrap

object CoppelCase {

  val separator = "|"
  val root = "http://www.coppel.com"

  abstract class Page(id: String, path: String, name: String) {
    override def toString: String = id+separator+root+path+separator+name
    def header: String
  }

  case class Department(id: String, path: String, name: String)  extends Page(id,path,name) {
    def header: String = "#"+"id"+separator+"url"+separator+"name"
  }

  case class Category(id: String, path: String, name: String, parent: String) extends Page(id,path,name) {
    override def toString: String = id+separator+root+path+separator+name+separator+parent
    def header: String = "#"+"id"+separator+"url"+separator+"name"+separator+"parent"
  }

  case class Product(id: String, path: String, name: String, parent: String,
  price: Int, twoWeeksPrice: Int, twoWeeksNumber: Int, twoWeeksPayment: Int) 
  extends Page(id, path, name) {
    override def toString: String = {
      id+separator+root+path+separator+name+separator+parent+separator+
      price+separator+twoWeeksPrice+separator+twoWeeksPayment+separator+twoWeeksNumber
    }
    def header: String = {
      "#"+"id"+separator+"url"+separator+"name"+separator+"parent"+separator+
      "price"+separator+"twoWeeksPrice"+separator+"twoWeeksPayment"+separator+"twoWeeksNumber"
    }
  }

}
