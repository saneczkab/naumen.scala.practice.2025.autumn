package service

import play.api.inject._
import play.api.inject.SimpleModule

class JobAggregationSchedulerModule extends SimpleModule(bind[JobAggregationScheduler].toSelf.eagerly())
