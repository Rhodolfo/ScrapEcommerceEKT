object Famsa {

  import com.rho.scrap.FamsaHTTP.getPages
  import com.rho.scrap.FamsaClasses.{Page,getBottomLevels}

  val prefix = "[Famsa] "

  def main(args: Array[String]) = {
    val pages = getPages
    val bottomLevels = getBottomLevels(pages)
    val topLevels = pages.filter(_.levels.size == 1)
    topLevels.foreach(println)
    bottomLevels.foreach(println)
  }

}
