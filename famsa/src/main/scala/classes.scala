package com.rho.scrap 

object FamsaClasses {
  
  private def stripLeading(s: String) = "^/{1,}".r replaceAllIn(s,"")
  private def stripTrailing(s: String) = "/{1,}$".r replaceAllIn(s,"") 
  private def stripSlash(s: String) = {
    stripLeading(stripTrailing("/{2,}".r replaceAllIn(s,"/")))
  }

  private def concatPaths(a: String, b: String) = if (a.isEmpty) "/"+b else a+"/"+b
  private def cleanName(s: String) = "(<.+?>|^\\s*?-\\s{1,}|^\\s{1,})".r replaceAllIn(s,"")

  class Page(rawName: String, rawPath: String) {
    val name = cleanName(rawName)
    val levels: Array[String] = stripSlash(rawPath).split("/")
    def path: String = levels.foldLeft[String]("")(concatPaths)
    override def toString = name+": "+path
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




}
