package com.avast.metrics.scalaapi.api

import scala.concurrent.Future

trait TimerPair {
  def time[A](block: => A): A

  def time[A](future: => Future[A]): Future[A]
}
