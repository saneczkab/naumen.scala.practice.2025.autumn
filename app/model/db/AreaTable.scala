package model.db

import model.Area
import slick.jdbc.PostgresProfile.api._

class AreaTable(tag: Tag) extends Table[Area](tag, "area")  {
    def hhId = column[Long]("hh_id", O.PrimaryKey)
    def name = column[String]("name")

    def * = (hhId, name) <> (Area.tupled, Area.unapply)

}
