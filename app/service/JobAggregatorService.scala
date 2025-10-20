package service

import model._
import model.db.DBTables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsValue, Reads, __}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import play.api.libs.ws.WSClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@Singleton
class JobAggregatorService @Inject()(ws: WSClient, val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {
    private def saveJob(job: Job, keywordStr: String, areaId: Long): Future[Unit] ={
      db.run(jobTable += job).map(_ => ())(scala.concurrent.ExecutionContext.Implicits.global)

      val keywordId = Await.result(upsertKeyword(keywordStr), Duration.Inf)
      val jobKeyword = JobKeyword(job.hhId, keywordId)
      db.run(jobKeywordTable += jobKeyword).map(_ => ())(scala.concurrent.ExecutionContext.Implicits.global)

      val jobId = job.hhId
      db.run(jobAreaTable += JobArea(jobId, areaId)).map(_ => ())(scala.concurrent.ExecutionContext.Implicits.global)
    }

  private def upsertKeyword(keyword: String): Future[Long] = {
      val existingKeywordId = db.run(keywordTable.filter(_.word === keyword).map(_.id).result.headOption)

      existingKeywordId.flatMap {
        case Some(id) => Future.successful(id)
        case None =>
          val newId = 0L
          db.run(keywordTable += Keyword(newId, keyword))
            .map(_ => newId)(scala.concurrent.ExecutionContext.Implicits.global)
      }(scala.concurrent.ExecutionContext.Implicits.global)
    }

    private implicit val pageReads: Reads[Page] = (
        (__ \ "page").read[Int] and
        (__ \ "pages").read[Int] and
        (__ \ "per_page").read[Int] and
        (__ \ "found").read[Int]
      )(Page.apply _)

    private implicit val jobReads: Reads[Job] = (
        (__ \ "id" ).read[String] and
        (__ \ "name").read[String] and
        (__ \ "snippet" \ "requirement").readNullable[String] and
        (__ \ "snippet" \ "responsibility").readNullable[String] and
        (__ \ "salary" \ "from").readNullable[Int] and
        (__ \ "salary" \ "to").readNullable[Int] and
        (__ \ "salary" \ "currency").readNullable[String] and
        (__ \ "salary" \ "gross").readNullable[Boolean] and
        (__ \ "alternate_url").read[String] and
        (__ \ "area" \ "id").read[String]
      ) { (hhId: String,
          title: String,
          requirement: Option[String],
          responsibility: Option[String],
          salaryFrom: Option[Int],
          salaryTo: Option[Int],
          salaryCurrency: Option[String],
          salaryGross: Option[Boolean],
           url: String,
           area: String) =>
      Job(hhId.toLong, title, requirement, responsibility, salaryFrom, salaryTo, salaryCurrency,
        salaryGross, url, area.toLong)
    }

    def fetchJobs(keyword: String, areaId: Long): Future[Seq[Job]] = {
      val result = fetchJobPage(keyword, areaId, 0)

      result.map { jobs =>
        jobs.foreach { job =>
          saveJob(job, keyword, areaId)
        }
      }(scala.concurrent.ExecutionContext.Implicits.global)

      result
    }

    private def fetchJobPage(keyword: String, areaId: Long, page: Int): Future[Seq[Job]] = {
      ws.url("https://api.hh.ru/vacancies")
        .addQueryStringParameters(
          "text" -> keyword,
          "area" -> areaId.toString,
          "page" -> page.toString
        )
        .addHttpHeaders("User-Agent" -> "com.job-aggregator")
        .get()
        .flatMap { response =>
          val pageInfo = response.json.as[Page]
          val jobs = (response.json \ "items").as[Seq[Job]]
          if (page < pageInfo.pages - 1) {
            fetchJobPage(keyword, areaId, page + 1)
              .map(nextJobs => jobs ++ nextJobs)(scala.concurrent.ExecutionContext.Implicits.global)
          } else {
            Future.successful(jobs)
          }
        }(scala.concurrent.ExecutionContext.Implicits.global)
    }

    def fetchAreaName(areaId: Long): Future[String] = {
      ws.url(s"https://api.hh.ru/areas/$areaId")
        .addHttpHeaders("User-Agent" -> "com.job-aggregator")
        .get()
        .map { response =>
          (response.json \ "name").as[String]
        }(scala.concurrent.ExecutionContext.Implicits.global)
    }

    def fetchAreas(): Future[JsValue] = {
      ws.url(s"https://api.hh.ru/areas")
        .addHttpHeaders("User-Agent" -> "com.job-aggregator")
        .get()
        .map { response =>
          response.json
        }(scala.concurrent.ExecutionContext.Implicits.global)
    }
}
