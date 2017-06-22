object Walmart {

  import com.rho.scrap.WalmartClasses.{Department,Product}
  import com.rho.scrap.WalmartHTTP.{getMenu}
  import com.rho.file.quickFunc.{writeToFile,makeDirectory}
  import com.rho.file.RhoCheck

  val prefix = "[Walmart] "
  val datdir = "data/"
  val prodfile = "walmart_data"
  val checkpoint = new RhoCheck(datdir+"walmart_checkpoints")

  def main(args: Array[String]): Unit = {

    makeDirectory(datdir)

    System.out.println(prefix+"Fetching department list")
    var deps = getMenu
    System.out.println(prefix+"Done")

    System.out.println(prefix+"Fetching line list")
    val lineMap = deps.map(e => (e,e.lines))
    System.out.println(prefix+"Done")

    System.out.println(prefix+"Fetching product list")
    lineMap.foreach(fetchProducts)
    System.out.println(prefix+"Done")

  }



  def fetchProducts(pair: (Department,List[Department])): Unit = pair match {
    case (dep,lines) => {
      val event = dep.departmentName
      if (!checkpoint.checkEvent(event)) {
        System.out.println(prefix+"Department "+event+" with "+lines.size+" lines")
        saveProducts(lines.flatMap{l => l.products})
        System.out.println(prefix+"Done with department "+dep.departmentName)
        checkpoint.recordEvent(event)
      } else {
        System.out.println(prefix+"Department "+event+" has already been done, skipping")
      }
    }
  }



  def saveProducts(products: List[Product]): Unit = {
    def concat(a: String, b: Product) = if (a.isEmpty) b.toString else a+"\n"+b.toString
    val (append,string) = {
      if (checkpoint.events.isEmpty) (false,products.head.header+"\n"+products.foldLeft[String]("")(concat))
      else (true,products.foldLeft[String]("")(concat))
    }
    writeToFile(datdir+prodfile,string,encoding="UTF-8",append=append)
  }



}
