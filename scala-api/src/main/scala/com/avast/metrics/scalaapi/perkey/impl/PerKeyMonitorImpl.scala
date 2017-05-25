package com.avast.metrics.scalaapi.perkey.impl

import com.avast.metrics.api
import com.avast.metrics.scalaapi._
import com.avast.metrics.scalaapi.perkey.{PerKeyMonitor, PerKeyOps}

/**
  * Adds `perKey` method, otherwise just forward method calls to [[com.avast.metrics.scalaapi.Monitor]]
  */
private[perkey] class PerKeyMonitorImpl(monitor: com.avast.metrics.scalaapi.Monitor, perKeyOps: PerKeyOps) extends PerKeyMonitor {
  override def perKey: PerKeyOps = perKeyOps

  override def named(name: String): Monitor = monitor.named(name)

  override def named(name: String, name2: String, names: String*): Monitor = monitor.named(name, name2, names: _*)

  override def getName: String = monitor.getName

  override def meter(name: String): Meter = monitor.meter(name)

  override def counter(name: String): Counter = monitor.counter(name)

  override def timer(name: String): Timer = monitor.timer(name)

  override def timerPair(name: String): TimerPair = monitor.timerPair(name)

  override def gauge[A](name: String)(gauge: () => A): Gauge[A] = monitor.gauge(name)(gauge)

  override def gauge[A](name: String, replaceExisting: Boolean)(gauge: () => A): Gauge[A] = monitor.gauge(name, replaceExisting)(gauge)

  override def histogram(name: String): Histogram = monitor.histogram(name)

  override def asJava: api.Monitor = monitor.asJava

  override def close(): Unit = monitor.close()
}
