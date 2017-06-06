package com.rho.scrap 

object FamsaClasses {
  
  private def stripLeading(s: String) = "^/{1,}".r replaceAllIn(s,"")
  private def stripTrailing(s: String) = "/{1,}$".r replaceAllIn(s,"") 
  def stripSlash(s: String) = {
    stripLeading(stripTrailing("/{2,}".r replaceAllIn(s,"/")))
  }



  def concat(s: String)(a: String, b: String) = if (a.isEmpty) b else a+s+b
  private def cleanName(s: String) = "(<.+?>|^\\s*?-\\s{1,}|^\\s{1,})".r replaceAllIn(s,"")
  def separator = "|"
  def scapedSeparator = "\\|"



  class Page(rawPath: String) {
    val levels: Array[String] = stripSlash(rawPath).split("/")
    def path: String = "/"+levels.foldLeft[String]("")(concat("/")(_,_))
    override def toString = stripLeading(path)
  }



  case class Item(id: String, name: String, parentPage: Page, price: Int, regularPrice: Int) {
    def header = "# id"+separator+"name"+separator+"parentPage"+separator+"price"+separator+"regularPrice"
    override def toString = id+separator+name+separator+parentPage+separator+price+separator+regularPrice
  }



  def getBottomLevels(pages: List[Page]): List[Page] = {
    def matchLevels(parent: Array[String], child: Array[String]): Boolean = {
      if (child.size <= parent.size) false
      else (parent zip child.take(parent.size)).forall{case(p,q) => p==q}
    }
    def existsSubPage(p: Page): Boolean = {
      pages.filter(_.levels.size>p.levels.size).exists(c => matchLevels(p.levels,c.levels))
    }
    pages.filter(x => !existsSubPage(x))
  }

  def getTopLevels(pages: List[Page]): List[Page] = pages.filter(_.levels.size == 1)

  def groupLevels(children: List[Page]): Map[Page,List[Page]] = {
    children.groupBy(_.levels.head).map{case (k,v) => (new Page(k),v)}.toMap
  }

}
