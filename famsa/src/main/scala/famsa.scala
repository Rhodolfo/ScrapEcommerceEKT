object Famsa {

  import com.rho.scrap.FamsaHTTP.getPages
  import com.rho.scrap.FamsaClasses.Page

  val prefix = "[Famsa] "

  def main(args: Array[String]) = {
    getPages.foreach(println)
  }

}
