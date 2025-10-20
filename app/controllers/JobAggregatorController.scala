package controllers

import javax.inject._
import play.api.mvc._
import service.JobAggregatorService

import scala.concurrent.ExecutionContext

@Singleton
class JobAggregatorController @Inject()
(val controllerComponents: ControllerComponents, jobAggregatorService: JobAggregatorService)
(implicit ec: ExecutionContext) extends BaseController {
  def index() = Action.async { implicit request: Request[AnyContent] =>
    val responseText = "Service is running. Pages:\n" +
      "/fetch?text=java&area=1 - Fetch jobs by keyword and area\n" +
      "/areas - List of area IDs\n" +
      "/areas/{id} - Area name by ID"
    scala.concurrent.Future.successful(Ok(responseText))
  }

  def fetchJobs(keyword: String, areaId: Long) =
    Action.async { implicit request: Request[AnyContent] =>
    jobAggregatorService.fetchJobs(keyword, areaId).map(_ => Ok("Jobs fetched and saved"))
  }

  def fetchAreas() = Action.async { implicit request: Request[AnyContent] =>
    jobAggregatorService.fetchAreas().map(areas => Ok(areas))
  }

  def fetchAreaName(areaId: Long) = Action.async { implicit request: Request[AnyContent] =>
    jobAggregatorService.fetchAreaName(areaId).map(name => Ok(name))
  }
}
