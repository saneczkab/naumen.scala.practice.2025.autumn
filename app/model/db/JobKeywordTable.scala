package model.db

import model.JobKeyword
import slick.jdbc.PostgresProfile.api._

class JobKeywordTable(tag: Tag) extends Table[JobKeyword](tag, "job_keyword") {
    def jobId = column[Long]("job_id")
    def keywordId = column[Long]("keyword_id")

    def * = (jobId, keywordId) <> (JobKeyword.tupled, JobKeyword.unapply)
}
