package com.avast.metrics.scalaapi

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

class TimerPair(success: api.Timer, failure: api.Timer) extends api.TimerPair {
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

  override def time[A](future: => Future[A])(implicit ec: ExecutionContext): Future[A] = {
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
