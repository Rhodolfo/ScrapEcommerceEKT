package com.rho.scrap

object FamsaHTTP {

  import com.rho.file.quickFunc.readIntoString
  import com.rho.scrap.FamsaParsing.readPages
  import com.rho.scrap.FamsaClasses.Page

  def getPages: List[Page] = {
    val body = readIntoString("famsa.html",encoding="UTF-8")
    readPages(body)
  }

}
