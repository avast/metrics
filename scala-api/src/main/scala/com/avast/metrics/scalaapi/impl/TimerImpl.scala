package com.avast.metrics.scalaapi.impl

import java.time.Duration

import com.avast.metrics.api.Timer.TimeContext
import com.avast.metrics.api.{Timer => JTimer}
import com.avast.metrics.scalaapi.Timer


private class TimerImpl(inner: JTimer) extends Timer {
  override def start(): TimeContext = inner.start()

  override def update(duration: Duration): Unit = inner.update(duration)

  override def name: String = inner.getName
}
