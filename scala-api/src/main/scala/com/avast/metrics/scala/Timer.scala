package com.avast.metrics.scala


import api.{Timer => ITimer}
import com.avast.metrics.api.{Timer => JTimer}
import com.avast.metrics.api.Timer.TimeContext

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import scala.util.control.NonFatal


class Timer(inner: JTimer)(implicit val ec: ExecutionContext) extends ITimer{
  override def start(): TimeContext = inner.start()

  override def update(duration: Duration): Unit = {
    inner.update(java.time.Duration.ofNanos(duration.toNanos))
  }

  override def update(duration: java.time.Duration): Unit = inner.update(duration)

  override def time[A](block: => A): A = {
    val ctx = start()
    val a = block
    ctx.stop()
    a
  }

  override def time[A](future: => Future[A]): Future[A] = {
    val ctx = start()
    val a = future
    a.onSuccess { case _ => ctx.stop() }
    a
  }

  override def time[A](block: => A, failure: ITimer): A = {
    val succCtx = start()
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

  override def time[A](future: => Future[A], failure: ITimer): Future[A] = {
    val succCtx = start()
    val failCtx = failure.start()
    try {
      val a = future
      a.onComplete {
        case Success(_) =>
          succCtx.stop()
        case Failure(thr) =>
          failCtx.stop()
      }
      a
    } catch {
      case NonFatal(thr) =>
        failCtx.stop()
        throw thr
    }
  }
}
