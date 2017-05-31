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
    println("ONE")
    System.out.println(prefix+"Fetching product data")
    deps.foreach { dep => 
      val one = getCategoryProducts(cats(dep.id).head.id,0)
      println(dep.id)
      println(one.size)
    }
    System.out.println(prefix+"Done")
  }

}
