package com.avast.metrics.scala

import java.util.function.Supplier

import com.avast.metrics.api.{Monitor => JMonitor}
import com.avast.metrics.scala.api.{Monitor => IMonitor}

import scala.concurrent.ExecutionContext

class Monitor(monitor: JMonitor)(implicit ec: ExecutionContext) extends IMonitor {
  override def named(name: String): IMonitor =
    new Monitor(monitor.named(name))

  override def named(name: String, name2: String, names: String*): IMonitor =
    new Monitor(monitor.named(name, name2, names: _*))

  override def getName: String = monitor.getName

  override def meter(name: String): Meter =
    new Meter(monitor.newMeter(name))

  override def counter(name: String): Counter =
    new Counter(monitor.newCounter(name))

  override def timer(name: String): Timer =
    new Timer(monitor.newTimer(name))

  override def timerPair(name: String): TimerPair =
    new TimerPair(
      timer(succName(name)),
      timer(failName(name))
    )

  override def gauge[A](name: String)(value: () => A): Gauge[A] =
    new Gauge[A](monitor.newGauge(name, new Supplier[A] {
      override def get(): A = value()
    }))

  override def histogram(name: String): Histogram =
    new Histogram(monitor.newHistogram(name))

  private def succName(base: String) = s"$base-success"
  private def failName(base: String) = s"$base-failure"
}
