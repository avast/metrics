package com.avast.metrics.scala.api

import scala.concurrent.Future

trait TimerPair {
  def time[A](block: => A): A

  def time[A](future: => Future[A]): Future[A]
}
