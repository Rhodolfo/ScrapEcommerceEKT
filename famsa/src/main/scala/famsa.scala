object Famsa {

  import java.io.File
  import com.rho.file.RhoCheck
  import com.rho.scrap.FamsaHTTP.{getPages,getItems}
  import com.rho.scrap.FamsaClasses.{Page,Item,getBottomLevels,getTopLevels,groupLevels}
  import com.rho.scrap.FamsaLogging.{savePages,saveItems,readPagesFile}
  import com.rho.scrap.FamsaLogging.{category_file,product_file,datadir}

  val prefix = "[Famsa] "
  val checkpoint = new RhoCheck(datadir+"famsa_checkpoint")

  def main(args: Array[String]): Unit = {
    val childrenMap = fetchCategoryMap
    fetchItems(childrenMap)
  }

  def fetchCategoryMap: Map[Page,List[Page]] = {
    val categories = {
      if ((new File(category_file)).exists) readPagesFile 
      else getBottomLevels(getPages)
    }
    savePages(categories)
    groupLevels(categories)
  }

  def fetchItems(childrenMap: Map[Page,List[Page]]): Unit = {
    def iterItems(children: List[Page], acc: List[Item] = Nil): List[Item] = {
      if (children.isEmpty) acc
      else iterItems(children.tail,getItems(children.head)++acc)
    }
    def iter(list: List[Page]): Unit = {
      if (list.isEmpty) System.out.println(prefix+"Done with Famsa")
      else {
        System.out.println(prefix+"Fetching items for department "+list.head)
        val currentPage = list.head
        if (!checkpoint.checkEvent(currentPage.toString)) {
          val items = iterItems(childrenMap(list.head))
          saveItems(items)
          checkpoint.recordEvent(currentPage.toString)
        }
        System.out.println(prefix+"Done fetching items for this department")
        iter(list.tail)
      }
    }
    iter(childrenMap.keys.toList)
  }

}
