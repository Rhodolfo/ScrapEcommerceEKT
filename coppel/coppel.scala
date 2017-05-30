object Coppel {

  import com.rho.scrap.CoppelHTTP._
  import com.rho.scrap.CoppelParsing._
  import com.rho.file.quickFunc.{writeToFile,readIntoString}

  def main(args: Array[String]): Unit = {
    println("Hi")
    val (deps,cats) = getTree

    deps.foreach {dep => 
      println(dep)
      cats(dep.id).foreach(println)
    }

    /*
    val departments = List(getDepartments.head)
    for (dep <- departments) {
      val categories = getCategories(dep.path)
      println(categories)

    }*/
    //val body = strip(readIntoString("catlist.html"))
    //println(readCategories(body))
    
  }
}
