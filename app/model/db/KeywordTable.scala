package model.db

import model.Keyword
import slick.jdbc.PostgresProfile.api._

class KeywordTable(tag: Tag) extends Table[Keyword](tag, "keyword") {
    def id = column[Long]("id", O.PrimaryKey)
    def word = column[String]("word")

    def * = (id, word) <> (Keyword.tupled, Keyword.unapply)
}
