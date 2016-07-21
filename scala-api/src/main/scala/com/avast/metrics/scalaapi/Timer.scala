package com.avast.metrics.scalaapi

import java.time.Duration

import com.avast.metrics.api.Timer.TimeContext
import com.avast.metrics.api.{Timer => JTimer}

import scala.concurrent.ExecutionContext


class Timer(inner: JTimer)(implicit val ec: ExecutionContext) extends api.Timer {
  override def start(): TimeContext = inner.start()

  override def update(duration: Duration): Unit = inner.update(duration)

  override def name: String = inner.getName
}
