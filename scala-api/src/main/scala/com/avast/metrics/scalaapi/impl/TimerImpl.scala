package com.avast.metrics.scalaapi.impl

import java.time.{Duration => JDuration}
import com.avast.metrics.api.Timer.TimeContext
import com.avast.metrics.api.{Timer => JTimer}
import com.avast.metrics.scalaapi.Timer

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

private class TimerImpl(inner: JTimer) extends Timer {

  override def start(): TimeContext = inner.start()

  override def update(duration: JDuration): Unit = inner.update(duration)

  override def update(duration: Duration): Unit = update(JDuration.ofNanos(duration.toNanos))

  override def name: String = inner.getName

  override def time[A](block: => A): A = {
    val context = inner.start()
    try {
      block
    } finally {
      context.stop()
    }
  }

  override def time[A](future: => Future[A])(implicit ec: ExecutionContext): Future[A] = {
    val context = inner.start()
    try {
      future andThen { case _ =>
        context.stop()
      }
    } catch {
      case NonFatal(ex) =>
        context.stop()
        throw ex
    }
  }

}
