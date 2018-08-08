package com.avast.metrics.scalaapi


import java.time.Duration

import scala.concurrent.{ExecutionContext, Future}

trait TimerPair {

  trait TimeContext {
    def stop(): Unit
    def stopFailure(): Unit
  }

  def start(): TimeContext

  def update(duration: Duration): Unit

  def updateFailure(duration: Duration): Unit

  def time[A](block: => A): A

  def time[A](future: => Future[A])(implicit ec: ExecutionContext): Future[A]

}
