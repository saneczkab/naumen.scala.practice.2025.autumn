package model.db

import slick.lifted.TableQuery

object DBTables {
    val jobTable = TableQuery[JobTable]
    val areaTable = TableQuery[AreaTable]
    val keywordTable = TableQuery[KeywordTable]
}
