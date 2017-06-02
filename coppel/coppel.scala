object Coppel {

  import com.rho.scrap.CoppelCase.{Department,Category}
  import com.rho.scrap.CoppelHTTP.{getTree,getCategoryProducts}
  import com.rho.scrap.CoppelLogging.{saveCollection}

  val prefix = "[Main] "

  def main(args: Array[String]): Unit = {
    val (departments,categoryMap) = fetchIndex
    val productMap = fetchProducts(departments, categoryMap)
  }

  def fetchIndex: (List[Department],Map[String,List[Category]]) = {
    System.out.println(prefix+"Fetching departments and categories")
    val (deps,cats) = getTree
    saveCollection(deps)
    saveCollection(for {dep <- deps; cat <- cats(dep.id)} yield cat)
    (deps,cats)
  }

  def fetchProducts(departments: List[Department], categoryMap: Map[String,List[Category]]):
  Map[String,List[Product]] = {
    def iterDept(deps: List[Department], acc: Map[String,List[Product]]):
    Map[String,List[Product]] = {
      if (deps.isEmpty) acc 
      else {
        val id = deps.head.id
        System.out.println(prefix+"Fetching product list for department "+id)
        val products = (for {
          cat<-categoryMap(id)
        } yield {
          getCategoryProducts(cat.id)
        }).flatten
        saveCollection(products,id)
        iterDept(deps.tail, acc+(id->products))
      }
    }
    iterDept(departments,Map())
  }

}
