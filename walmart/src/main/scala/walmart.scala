object Walmart {

  import com.rho.scrap.WalmartClasses.{Department,Product}
  import com.rho.scrap.WalmartHTTP.{getMenu}

  val prefix = "[Walmart] "

  def main(args: Array[String]): Unit = {
    System.out.println(prefix+"Fetching department list")
    var deps = getMenu
    System.out.println(prefix+"Done")
    def printSubs(d: Department): Unit = d.Elements.foreach(e => println(e.departmentName+" "+e.url))
    def printIt(d: Department): Unit = println(d.departmentName+" "+d.url)
    System.out.println(prefix+"Fetching line list")
    val lineMap = deps.map(e => (e,e.lines))
    System.out.println(prefix+"Done")

    // Test
    fetchProducts(lineMap.head)

  }



  def fetchProducts(pair: (Department,List[Department])): Unit = pair match {
    case (dep,lines) => {
      System.out.println(prefix+"Department "+dep.departmentName+" with "+lines.size+" lines")
      val products = lines.flatMap(line => line.products)
      saveProducts(products)
      System.out.println(prefix+"Done with department "+dep.departmentName)
    }
  }



  def saveProducts(products: List[Product]): Unit = {
    def concat(a: String, b: Product) = if (a.isEmpty) b.toString else a+"\n"+b.toString
    println(products.foldLeft[String]("")(concat))
  }



}
