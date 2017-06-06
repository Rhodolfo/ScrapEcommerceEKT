package com.rho.scrap

object FamsaParsing {
  
  import scala.util.matching.Regex
  import com.rho.scrap.FamsaClasses.Page

  val prefix = "[Parsing] "

  private def stripLeadingSpaces(s: String) = "^\\s{1,}".r replaceAllIn(s,"")
  private def stripTrailingSpaces(s: String) = "\\s{1,}$".r replaceAllIn(s,"")
  private def trim(s: String) = stripLeadingSpaces(stripTrailingSpaces(s))
  private def stripSpaces(s: String) = "\\s".r replaceAllIn(s,"")

  def readPages(body: String): List[Page] = {
    def filterLinks(path: String): Boolean = {
      def matchBadPattern(pattern: String): Boolean = (pattern.r findFirstIn path) match {
        case Some(x) => true
        case None => false
      }
      val blacklist = List("centro-de-ayuda","garantias-y-devoluciones","venta-empresarial")
      !(blacklist exists matchBadPattern)
    }
    val regex = new Regex("<li.+?<a href=(\"|')(\\W.+?)(\"|').*?>(.+?)<")
    println(stripSpaces("    stripMe      ")+"END")
    (for {
      entry <- (regex findAllMatchIn body).toList
    } yield {
      (trim(entry.group(4)),trim(entry.group(2)))
    }).filter {
      case(name,link) => filterLinks(link)
    }.map {
      case(name,link) => new Page(name,link)
    }
  }

}
