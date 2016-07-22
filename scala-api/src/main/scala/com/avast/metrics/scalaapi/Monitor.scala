package com.avast.metrics.scalaapi

import java.util.function.Supplier

import com.avast.metrics.api.{Naming, Monitor => JMonitor}
import com.avast.metrics.test.NoOpMonitor

import scala.concurrent.ExecutionContext


object Monitor {
  def apply(monitor: JMonitor)(implicit ec: ExecutionContext): api.Monitor = new Monitor(monitor, Naming.defaultNaming())
  def noOp(implicit ec: ExecutionContext): api.Monitor = {
    apply(NoOpMonitor.INSTANCE)
  }
}

class Monitor(monitor: JMonitor, naming: Naming)(implicit ec: ExecutionContext) extends api.Monitor {
  override def named(name: String): api.Monitor =
    new Monitor(monitor.named(name), naming)

  override def named(name: String, name2: String, names: String*): api.Monitor =
    new Monitor(monitor.named(name, name2, names: _*), naming)

  override def getName: String = monitor.getName

  override def meter(name: String): api.Meter =
    new Meter(monitor.newMeter(name))

  override def counter(name: String): api.Counter =
    new Counter(monitor.newCounter(name))

  override def timer(name: String): api.Timer =
    new Timer(monitor.newTimer(name))

  override def timerPair(name: String): api.TimerPair =
    new TimerPair(
      timer(naming.successTimerName(name)),
      timer(naming.failureTimerName(name))
    )

  override def gauge[A](name: String)(value: () => A): api.Gauge[A] =
    new Gauge[A](monitor.newGauge(name, new Supplier[A] {
      override def get(): A = value()
    }))

  override def histogram(name: String): api.Histogram =
    new Histogram(monitor.newHistogram(name))
}
