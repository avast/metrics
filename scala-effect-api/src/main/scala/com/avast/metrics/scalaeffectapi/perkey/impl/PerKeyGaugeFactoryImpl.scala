package com.avast.metrics.scalaeffectapi.perkey.impl

import com.avast.metrics.scalaeffectapi.{Gauge, Monitor}
import com.avast.metrics.scalaeffectapi.perkey.{PerKeyGaugeFactory, PerKeyMetric}

import scala.collection.concurrent.TrieMap

class PerKeyGaugeFactoryImpl[F[_]](monitor: Monitor[F]) extends PerKeyGaugeFactory[F] {
  private def emptyMap[M] = TrieMap.empty[String, M]

  override def long(baseName: String): PerKeyMetric[Gauge[F, Long]] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Gauge[F, Long]](emptyMap[Gauge[F, Long]], instanceBuilder.gauge.long)
  }

  override def double(baseName: String): PerKeyMetric[Gauge[F, Double]] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Gauge[F, Double]](emptyMap[Gauge[F, Double]], instanceBuilder.gauge.double)
  }
}
