package com.avast.metrics.scalaapi.impl

import java.util.function.Supplier

import com.avast.metrics.api.{Naming, Monitor => JMonitor}
import com.avast.metrics.scalaapi._

private[scalaapi] class MonitorImpl(monitor: JMonitor, naming: Naming) extends Monitor {

  override def named(name: String): Monitor = new MonitorImpl(monitor.named(name), naming)

  override def named(name: String, name2: String, names: String*): Monitor = new MonitorImpl(monitor.named(name, name2, names: _*), naming)

  override def getName: String = monitor.getName

  override def meter(name: String): Meter = new MeterImpl(monitor.newMeter(name))

  override def counter(name: String): Counter = new CounterImpl(monitor.newCounter(name))

  override def timer(name: String): Timer = new TimerImpl(monitor.newTimer(name))

  override def timerPair(name: String): TimerPair = new TimerPairImpl(
    timer(naming.successTimerName(name)),
    timer(naming.failureTimerName(name))
  )

  override def gauge[A](name: String)(value: () => A): Gauge[A] = gauge(name, replaceExisting = false)(value)

  override def gauge[A](name: String, replaceExisting: Boolean)(value: () => A): Gauge[A] =
    new GaugeImpl[A](monitor.newGauge(name, replaceExisting, new Supplier[A] {
      override def get(): A = value()
    }))

  override def histogram(name: String): Histogram = new HistogramImpl(monitor.newHistogram(name))

  override def close(): Unit = monitor.close()

  override def asJava: JMonitor = monitor

}
