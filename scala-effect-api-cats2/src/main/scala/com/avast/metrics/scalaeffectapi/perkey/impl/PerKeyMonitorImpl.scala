package com.avast.metrics.scalaeffectapi.perkey.impl

import com.avast.metrics.{api, scalaapi}
import com.avast.metrics.scalaeffectapi._
import com.avast.metrics.scalaeffectapi.perkey.{PerKeyMonitor, PerKeyOps}

/** Adds `perKey` method, otherwise just forward method calls to [[com.avast.metrics.scalaeffectapi.Monitor]]
  */
private[perkey] class PerKeyMonitorImpl[F[_]](monitor: Monitor[F], perKeyOps: PerKeyOps[F]) extends PerKeyMonitor[F] {
  override def perKey: PerKeyOps[F] = perKeyOps

  override def named(name: String): Monitor[F] = monitor.named(name)

  override def named(name: String, name2: String, names: String*): Monitor[F] = monitor.named(name, name2, names: _*)

  override def getName: String = monitor.getName

  override def meter(name: String): Meter[F] = monitor.meter(name)

  override def counter(name: String): Counter[F] = monitor.counter(name)

  override def timer(name: String): Timer[F] = monitor.timer(name)

  override def timerPair(name: String): TimerPair[F] = monitor.timerPair(name)

  override def histogram(name: String): Histogram[F] = monitor.histogram(name)

  override def gauge: GaugeFactory[F] = monitor.gauge

  override def close(): Unit = monitor.close()

  override def asJava: api.Monitor = monitor.asJava

  override def asPlainScala: scalaapi.Monitor = monitor.asPlainScala

}
