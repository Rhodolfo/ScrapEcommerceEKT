package com.rho.scrap

object FamsaParsing {
  
  import scala.util.matching.Regex
  import com.rho.scrap.FamsaClasses.{Page,Item}
  import com.rho.scrap.FamsaHTTP.host

  val prefix = "[Parsing] "

  private def stripLeadingSpaces(s: String) = "^\\s{1,}".r replaceAllIn(s,"")
  private def stripTrailingSpaces(s: String) = "\\s{1,}$".r replaceAllIn(s,"")
  private def trim(s: String) = stripLeadingSpaces(stripTrailingSpaces(s))
  private def removeRoot(s: String) = ("^.*?"+host).r replaceAllIn(s,"")
  private def stripSpaces(s: String) = "\\s".r replaceAllIn(s,"")



  private def parseForLinks(body: String): List[Page] = {
    def filterLinks(path: String): Boolean = {
      def matchBadPattern(pattern: String): Boolean = (pattern.r findFirstIn path) match {
        case Some(x) => true
        case None => false
      }
      val blacklist = List("busqueda")
      if (path.isEmpty) false 
      else !(blacklist exists matchBadPattern)
    }
    val regex = new Regex("<a.+?href=(\"|')(.*?)(\"|').*?>(.+?)<")
    (for {
      entry <- (regex findAllMatchIn body).toList
    } yield {
      trim(removeRoot(entry.group(2)))
    }).filter {
      case(link) => filterLinks(link)
    }.map {
      case(link) => new Page(link)
    }
  }



  def readPages(body: String): List[Page] = {
    val trimmedBody = {
      "class=.MCategorias.+?</nav>".r findFirstIn body match {
        case Some(x) => x
        case None => throw new Error("Bad body trim")
      }
    }
    parseForLinks(trimmedBody)
  }



  def readItems(body: String, parentPage: Page): List[Item] = {
    import net.liftweb.json._
    import scala.math.round
    def parsePrice(s: String): Int = {
      val t = if (s.isEmpty) "0" else "(,|$|\\s)".r replaceAllIn(s,"")
      round(round(t.toDouble*100.0))
    }
    val check404 = {
      (">\\D*?404\\D*?<".r findFirstIn body) match {
        case Some(x) => true
        case None => false
      }
    }
    if (check404) Nil
    else {
      implicit val formats = DefaultFormats
      val regex = new Regex("objDataProd\\s*?=\\s*?(\\S.+?);")
      val json = (regex findFirstMatchIn body) match {
        case Some(x) => parse(x.group(1))
        case None => throw new Error("Item data not found")
      }
      for {
        entry @ JObject(x) <- json
        JField("idProducto",idProducto) <- entry.obj
        JField("nombre",nombre) <- entry.obj
        JField("precio",precio) <- entry.obj
        JField("precioRegular",precioRegular) <- entry.obj
        JField("rating",rating) <- entry.obj
      } yield {
        Item(idProducto.extract[String], nombre.extract[String], parentPage,
        parsePrice(precio.extract[String]), parsePrice(precioRegular.extract[String]))
      }
    }
  }

  

  def readSubCategories(body: String): List[Page] = {
    val trimmedBody = {
      "id=.s-filtro_cat.+?</nav>".r findFirstIn body match {
        case Some(x) => x
        case None => throw new Error("Bad body trim")
      }
    }
    parseForLinks(trimmedBody)
  }

}
