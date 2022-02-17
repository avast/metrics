package com.avast.metrics.scalaeffectapi.perkey.impl

import com.avast.metrics.scalaeffectapi._
import com.avast.metrics.scalaeffectapi.perkey.{PerKeyGaugeFactory, PerKeyMetric, PerKeyOps}

import scala.collection.concurrent.TrieMap

private[perkey] class PerKeyOpsImpl[F[_]](monitor: Monitor[F]) extends PerKeyOps[F] {
  private def emptyMap[M] = TrieMap.empty[String, M]

  override def meter(baseName: String): PerKeyMetric[Meter[F]] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Meter[F]](emptyMap[Meter[F]], instanceBuilder.meter)
  }

  override def counter(baseName: String): PerKeyMetric[Counter[F]] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Counter[F]](emptyMap, instanceBuilder.counter)
  }

  override def timer(baseName: String): PerKeyMetric[Timer[F]] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Timer[F]](emptyMap, instanceBuilder.timer)
  }

  override def timerPair(baseName: String): PerKeyMetric[TimerPair[F]] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[TimerPair[F]](emptyMap, instanceBuilder.timerPair)
  }

  override def histogram(baseName: String): PerKeyMetric[Histogram[F]] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Histogram[F]](emptyMap, instanceBuilder.histogram)
  }

  override def gauge: PerKeyGaugeFactory[F] = {
    new PerKeyGaugeFactoryImpl[F](monitor)
  }

}
