package model.db

import model.JobArea
import slick.jdbc.PostgresProfile.api._

class JobAreaTable(tag: Tag) extends Table[JobArea](tag, "job_area") {
  def jobId = column[Long]("job_id")
  def areaId = column[Long]("area_id")

  def * = (jobId, areaId) <> (JobArea.tupled, JobArea.unapply)
}
