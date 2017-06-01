object Coppel {

  import com.rho.scrap.CoppelHTTP._
  import com.rho.scrap.CoppelParsing._
  import com.rho.file.quickFunc.{writeToFile,readIntoString}

  def main(args: Array[String]): Unit = {
    val prefix = "[Main] "
    System.out.println(prefix+"Fetching departments and categories")
    val (deps,cats) = getTree
    deps.foreach {dep => 
      println(dep)
      cats(dep.id).foreach(println)
    }
    System.out.println(prefix+"Fetching product list")
    val products = (for {
      dep <- deps
      cat <- cats(dep.id)
    } yield {
      (cat,getCategoryProducts(cat.id))
    }).toMap
    System.out.println(prefix+"Done")
  }

}
