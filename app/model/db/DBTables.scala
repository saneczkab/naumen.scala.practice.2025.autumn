package model.db

import slick.lifted.TableQuery

object DBTables {
    val jobTable = TableQuery[JobTable]
    val keywordTable = TableQuery[KeywordTable]
    val jobKeywordTable = TableQuery[JobKeywordTable]
    val jobAreaTable = TableQuery[JobAreaTable]
}
