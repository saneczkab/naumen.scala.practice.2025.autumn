package service

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.Configuration

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Duration

@Singleton
class JobAggregationScheduler @Inject()
(actorSystem: ActorSystem, config: Configuration, jobService: JobAggregatorService)(
  implicit executionContext: ExecutionContext
) {
  private val logger = play.api.Logger("application")

  private val times = {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val timeStrings = config.get[Seq[String]]("scheduler.times")
    timeStrings.map(LocalTime.parse(_, formatter))
  }
  private val areaIds = config.get[Seq[Long]]("scheduler.areaIds")
  private val keywords = config.get[Seq[String]]("scheduler.keywords")

  private val schedule = {
    times.map { time =>
      val delay = getDelay(time)
      logger.info(s"Scheduled jobs fetching at $time")
      actorSystem.scheduler.scheduleWithFixedDelay(delay.seconds, 24.hours) {
        () => runJobFetching()
      }
    }
  }

  private def getDelay(time: LocalTime): Long = {
    val now = LocalTime.now()
    val timeDiff = Duration.between(now, time)
    if (timeDiff.isNegative) {
      timeDiff.plusDays(1).getSeconds
    } else {
      timeDiff.getSeconds
    }
  }

  private def runJobFetching(): Unit = {
    logger.info("Fetching jobs...")

    for {
      areaId <- areaIds
      keyword <- keywords
    } {
      logger.info(s"Fetching jobs for areaId $areaId and keyword $keyword")
      jobService.fetchJobs(keyword, areaId)
    }

    logger.info("Jobs fetched and saved")
  }
}
