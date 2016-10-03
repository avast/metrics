package com.avast.metrics.scalaapi

import java.time.Duration

import com.avast.metrics.api.Timer.TimeContext

import scala.concurrent.{ExecutionContext, Future}

trait Timer extends Metric {

  def start(): TimeContext

  def update(duration: Duration): Unit

  def time[A](block: => A): A

  def time[A](future: => Future[A])(implicit ec: ExecutionContext): Future[A]

}
