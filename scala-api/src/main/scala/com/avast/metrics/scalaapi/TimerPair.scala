package com.avast.metrics.scalaapi

import scala.concurrent.{ExecutionContext, Future}

trait TimerPair {

  def time[A](block: => A): A

  def time[A](future: => Future[A])(implicit ec: ExecutionContext): Future[A]

}
