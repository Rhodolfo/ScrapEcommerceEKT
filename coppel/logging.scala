package com.rho.scrap

object CoppelLogging {

  import com.rho.file.quickFunc.{writeToFile,readIntoList}
  import com.rho.scrap.CoppelCase.{Page,Department,Category,Product,separator}

  val datadir = "data/"

  def saveCollection(list: List[Page], parentId: String = ""): Unit = {
    if (list.isEmpty) throw new Error("Empty list")
    val (name,header) = list.head match {
      case x: Department => ("coppel_departments",x.header)
      case x: Category => ("coppel_categories",x.header)
      case x: Product => ("coppel_products_"+parentId,x.header)
    }
    def cat(a: String, b: String): String = if (a.isEmpty) b else a+"\n"+b
    val body = header + "\n" + list.map(_.toString).foldLeft[String]("")(cat)
    writeToFile(datadir+name,body,encoding="UTF-8")
  }

  def readCollection[Type](parentId: String = ""): List[Type] = {
    ???
  }

}
