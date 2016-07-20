package com.avast.metrics.scala

import api.{TimerPair => ITimerPair}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scala.util.control.NonFatal

class TimerPair(success: Timer, failure: Timer)(implicit ec: ExecutionContext) extends ITimerPair {
  override def time[A](block: => A): A = {
    val succCtx = success.start()
    val failCtx = failure.start()

    try {
      val a = block
      succCtx.stop()
      a
    } catch {
      case NonFatal(thr) =>
        failCtx.stop()
        throw thr
    }
  }

  override def time[A](future: => Future[A]): Future[A] = {
    val succCtx = success.start()
    val failCtx = failure.start()
    try {
      val a = future
      a andThen {
        case Success(_) =>
          succCtx.stop()
        case Failure(thr) =>
          failCtx.stop()
      }
    } catch {
      case NonFatal(thr) =>
        failCtx.stop()
        throw thr
    }
  }
}
