package com.avast.metrics.scalaapi.perkey.impl

import com.avast.metrics.scalaapi._
import com.avast.metrics.scalaapi.perkey.{PerKeyMetric, PerKeyOps}

import scala.collection.concurrent.TrieMap

private[perkey] class PerKeyOpsImpl(monitor: Monitor) extends PerKeyOps {
  private def emptyMap[M] = TrieMap.empty[String, M]

  override def meter(baseName: String): PerKeyMetric[Meter] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Meter](emptyMap[Meter], instanceBuilder.meter)
  }

  override def counter(baseName: String): PerKeyMetric[Counter] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Counter](emptyMap, instanceBuilder.counter)
  }

  override def timer(baseName: String): PerKeyMetric[Timer] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Timer](emptyMap, instanceBuilder.timer)
  }

  override def timerPair(baseName: String): PerKeyMetric[TimerPair] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[TimerPair](emptyMap[TimerPair], instanceBuilder.timerPair)
  }

  override def gauge[A](baseName: String)(gauge: () => A): PerKeyMetric[Gauge[A]] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Gauge[A]](emptyMap[Gauge[A]], instanceBuilder.gauge(_)(gauge))
  }

  override def gauge[A](baseName: String, replaceExisting: Boolean)(gauge: () => A): PerKeyMetric[Gauge[A]] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Gauge[A]](emptyMap[Gauge[A]], instanceBuilder.gauge(_, replaceExisting)(gauge))
  }

  override def histogram(baseName: String): PerKeyMetric[Histogram] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Histogram](emptyMap, instanceBuilder.histogram)
  }
}
