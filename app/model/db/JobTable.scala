package model.db

import model.Job
import slick.jdbc.PostgresProfile.api._

class JobTable(tag: Tag) extends Table[Job](tag, "job") {
    def hhId = column[Long]("hh_id", O.PrimaryKey)
    def title = column[String]("title")
    def requirement = column[Option[String]]("requirement")
    def responsibility = column[Option[String]]("responsibility")
    def salaryFrom = column[Option[Int]]("salary_from")
    def salaryTo = column[Option[Int]]("salary_to")
    def salaryCurrency = column[Option[String]]("salary_currency")
    def salaryGross = column[Option[Boolean]]("salary_gross")
    def url = column[String]("url")
    def areaId = column[Long]("area_id")

    def * =
      (hhId, title, requirement, responsibility, salaryFrom, salaryTo, salaryCurrency, salaryGross, url, areaId) <>
        (Job.tupled, Job.unapply)
}

