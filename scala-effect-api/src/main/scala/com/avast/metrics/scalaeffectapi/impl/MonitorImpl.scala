package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import com.avast.metrics.api.{Monitor => JMonitor, Naming}

import com.avast.metrics.scalaapi.{Monitor => SMonitor}
import com.avast.metrics.scalaeffectapi._

private[scalaeffectapi] class MonitorImpl[F[_]: Sync](monitor: SMonitor, naming: Naming) extends Monitor[F] {

  override def named(name: String): Monitor[F] = new MonitorImpl(monitor.named(name), naming)

  override def named(name: String, name2: String, names: String*): Monitor[F] =
    new MonitorImpl(monitor.named(name, name2, names: _*), naming)

  override def getName: String = monitor.getName

  override def meter(name: String): Meter[F] = new MeterImpl[F](monitor.meter(name))

  override def counter(name: String): Counter[F] = new CounterImpl(monitor.counter(name))

  override def timer(name: String): Timer[F] = new TimerImpl(monitor.timer(name))

  override def timerPair(name: String): TimerPair[F] =
    new TimerPairImpl(timer(naming.successTimerName(name)), timer(naming.failureTimerName(name)))

  override def histogram(name: String): Histogram[F] = new HistogramImpl(monitor.histogram(name))

  override def gauge: GaugeFactory[F] = new GaugeFactoryImpl(monitor)

  override def close(): Unit = monitor.close()

  override def asJava: JMonitor = monitor.asJava

  override def unwrap: SMonitor = monitor

}
