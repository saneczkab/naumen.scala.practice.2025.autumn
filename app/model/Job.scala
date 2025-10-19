package model

case class Job(hhId: Long, title: String, requirement: Option[String], responsibility: Option[String],
               salaryFrom: Option[Int], salaryTo: Option[Int], salaryCurrency: Option[String],
               salaryGross: Option[Boolean], url: String, areaId: Long)
