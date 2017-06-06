package com.rho.scrap 

object FamsaClasses {
  
  private def stripLeading(s: String) = "^/{1,}".r replaceAllIn(s,"")
  private def stripTrailing(s: String) = "/{1,}$".r replaceAllIn(s,"") 
  private def stripSlash(s: String) = {
    stripLeading(stripTrailing("/{2,}".r replaceAllIn(s,"/")))
  }

  private def concatPaths(a: String, b: String) = if (a.isEmpty) "/"+b else a+"/"+b

  class Page(val name: String, raw: String) {
    val levels: Array[String] = stripSlash(raw).split("/")
    def path: String = levels.foldLeft[String]("")(concatPaths)
    override def toString = name+": "+path
  }

}
