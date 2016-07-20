package com.avast.metrics.scala

import java.time.Duration

import com.avast.metrics.api.Timer.TimeContext
import com.avast.metrics.api.{Timer => JTimer}
import com.avast.metrics.scala.api.{Timer => ITimer}

import scala.concurrent.ExecutionContext


class Timer(inner: JTimer)(implicit val ec: ExecutionContext) extends ITimer{
  override def start(): TimeContext = inner.start()

  override def update(duration: Duration): Unit = inner.update(duration)

  override def name: String = inner.getName
}
