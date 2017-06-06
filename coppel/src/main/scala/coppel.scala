object Coppel {

  import com.rho.file.quickFunc.makeDirectory
  import com.rho.scrap.CoppelCase.{Department,Category,Item}
  import com.rho.scrap.CoppelHTTP.{getTree,getCategoryItems,getItemData}
  import com.rho.scrap.CoppelLogging.{saveCollection,readDepartments,readCategories,readItems,datadir}

  val prefix = "[Coppel] "

  def main(args: Array[String]): Unit = {
    makeDirectory(datadir)
    val (departments,categoryMap) = fetchIndex
    val productMap = fetchItems(departments,categoryMap)
    val sortedDepartments = sortDepartments(departments,categoryMap,productMap)
    fetchMissingData(sortedDepartments,productMap)
  }



  // Obtains all departments, then all categories grouped by department in the form of a map
  def fetchIndex: (List[Department],Map[String,List[Category]]) = {
    System.out.println(prefix+"Fetching departments and categories")
    val (datt,catt) = (readDepartments,readCategories)
    val (deps,cats) = {
      if (!datt.isEmpty && !catt.isEmpty) (datt,catt.groupBy(_.parent).toMap)
      else getTree
    }
    saveCollection(deps)
    saveCollection(for {dep <- deps; cat <- cats(dep.id)} yield cat)
    (deps,cats)
  }



  // Fetches all products grouped by department in the form of a map
  def fetchItems(departments: List[Department], categoryMap: Map[String,List[Category]]):
  Map[String,List[Item]] = {
    def iterDept(deps: List[Department], acc: Map[String,List[Item]]):
    Map[String,List[Item]] = {
      if (deps.isEmpty) acc 
      else {
        val id = deps.head.id
        System.out.println(prefix+"Fetching product list for department "+id)
        val products = {
          val attempt = readItems(id)
          if (!attempt.isEmpty) {
            attempt
          } else { 
            (for {
              cat<-categoryMap(id)
            } yield {
              getCategoryItems(cat.id)
            }).flatten
          }
        }
      saveCollection(products,id)
        iterDept(deps.tail, acc+(id->products))
      }
    }
    iterDept(departments,Map())
  }



  // I want to know how many products I need to revisit
  // And I want to know in what order I should do this
  def sortDepartments(departments: List[Department], 
  categoryMap: Map[String,List[Category]], productMap: Map[String,List[Item]]): List[Department] = {
    val numbers = for {
      dep <- departments
    } yield {
      (dep,(productMap(dep.id).filter(!_.hasCreditData).size,productMap(dep.id).size))
    }
    def sort_pair(p: (Any,Int), q: (Any,Int)): Boolean = p._2<q._2
    def sum_pair(p: (Int,Int), q: (Int,Int)): (Int,Int) = (p._1+q._1,p._2+q._2)
    System.out.println(prefix+"Printing out number of products with missing credit data")
    numbers.foreach {case(dep,pair) => 
      System.out.println(prefix+dep.name+" missingDataItems/totalNoItems = "+pair._1+"/"+pair._2)
    }
    val summed = numbers.unzip._2.foldLeft[(Int,Int)]((0,0))(sum_pair)
    System.out.println(prefix+"Total missingDataItems/totalNoItems = "+summed._1+"/"+summed._2)
    numbers.map(pair => (pair._1,pair._2._1)).sortWith(sort_pair).unzip._1
  }



  // Skeleton
  def fetchMissingData(departments: List[Department], productMap: Map[String,List[Item]]): Unit = {
    val every = 50
    def fixMissing(missing: List[Item], haveit: List[Item], id: String, counter: Int): 
    List[Item] = {
      if (missing.isEmpty) haveit 
      else {
        val new_missing = missing.tail
        val new_haveit  = getItemData(missing.head) :: haveit
        if (counter>=every) {
          System.out.println(prefix+"Saving results every "+every+" requests")
          saveCollection(new_missing++new_haveit,id)
          fixMissing(new_missing,new_haveit,id,0)
        } else {
          fixMissing(new_missing,new_haveit,id,counter+1)
        }
      }
    }
    def iter(deps: List[Department]) {
      if (deps.isEmpty) System.out.println(prefix+"Done fetching missing data")
      else {
        val id = deps.head.id
        val (has,hasNot) = productMap(id).partition(_.hasCreditData)
        System.out.println(prefix+"Fetching missing credit data for department "+id)
        val products = fixMissing(hasNot,has,id,0)
        saveCollection(products,id)
        iter(deps.tail)
      }
    }
    iter(departments)
  }

}
