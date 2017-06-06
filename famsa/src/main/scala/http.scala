package com.rho.scrap

object FamsaHTTP {

  import com.rho.file.quickFunc.readIntoString
  import com.rho.scrap.FamsaParsing.readPages
  import com.rho.scrap.FamsaClasses.Page

  private def strip(s: String) = "(\\t|\\n|\\r)".r replaceAllIn(s,"")

  def getPages: List[Page] = {
    val body = strip(readIntoString("famsa.html",encoding="UTF-8"))
    readPages(body)
  }

}
