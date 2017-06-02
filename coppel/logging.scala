package com.rho.scrap

object CoppelLogging {

  import scala.reflect.ClassTag
  import com.rho.file.quickFunc.{writeToFile,readIntoList}
  import com.rho.scrap.CoppelCase.{Page,Department,Category,Product,separator}

  val datadir = "data/"
  val prefix_dep = "coppel_departments"
  val prefix_cat = "coppel_categories"
  val prefix_pro = "coppel_products"

  def saveCollection(list: List[Page], parentId: String = ""): Unit = {
    if (list.isEmpty) throw new Error("Empty list")
    val (name,header) = list.head match {
      case x: Department => (prefix_dep,x.header)
      case x: Category => (prefix_cat,x.header)
      case x: Product => (prefix_pro+"_"+parentId,x.header)
    }
    def cat(a: String, b: String): String = if (a.isEmpty) b else a+"\n"+b
    val body = header + "\n" + list.map(_.toString).foldLeft[String]("")(cat)
    writeToFile(datadir+name,body,encoding="UTF-8")
  }

  def readDepartments: List[Department] = {
    readIntoList(datadir+prefix_dep,encoding="UTF-8").
    tail.map(_.split("\\"+separator)).map(e => Department(e(0),e(1),e(2)))
  }

  def readCategories: List[Category] = {
    readIntoList(datadir+prefix_cat,encoding="UTF-8").
    tail.map(_.split("\\"+separator)).map(e => Category(e(0),e(1),e(2),e(3))) 
  }

  def readProducts(departmentId: String): List[Product] = {
    readIntoList(datadir+prefix_cat+"_"+departmentId,encoding="UTF-8").
    tail.map(_.split("\\"+separator)).map{e => 
      Product(e(0),e(1),e(2),e(3),e(4).toInt,e(5).toInt,e(6).toInt,e(7).toInt)
    }
  }

  /* This will come in handy, will delete in final build
  def readCollection[T](implicit tf: ClassTag[T]): List[T] = {
    val cl = tf.runtimeClass
    if (classOf[Department] isAssignableFrom cl) {
      readIntoList(datadir+prefix_dep,encoding="UTF-8").
      tail.map(_.split("\\"+separator)).map(e => Department(e(0),e(1),e(2)))
      Nil
    } else if (classOf[Category] isAssignableFrom cl) {
      Nil
    } else {
      Nil
    }
  }
  */

}
