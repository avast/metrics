package com.avast.metrics.scalaapi.impl

import java.time.Duration

import com.avast.metrics.api.Timer.TimeContext
import com.avast.metrics.api.{Timer => JTimer}
import com.avast.metrics.scalaapi.Timer

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

private class TimerImpl(inner: JTimer) extends Timer {

  override def start(): TimeContext = inner.start()

  override def update(duration: Duration): Unit = inner.update(duration)

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
