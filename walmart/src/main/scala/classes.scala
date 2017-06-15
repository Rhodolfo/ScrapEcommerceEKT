package com.rho.scrap

object WalmartClasses {


  case class Department(
    Elements: List[Department],
    Id: String, 
    Description: String,
    departmentName: String, 
    url: String)

  case class Menu(Elements: List[Department], MenuName: String)

}
