package com.avast.metrics.scalaeffectapi.perkey.impl

import com.avast.metrics.scalaeffectapi.{Gauge, Monitor, SettableGauge}
import com.avast.metrics.scalaeffectapi.perkey.{PerKeyGaugeFactory, PerKeyMetric}

import scala.collection.concurrent.TrieMap

class PerKeyGaugeFactoryImpl[F[_]](monitor: Monitor[F]) extends PerKeyGaugeFactory[F] {
  private def emptyMap[M] = TrieMap.empty[String, M]

  override def long(baseName: String, replaceExisting: Boolean = false): PerKeyMetric[SettableGauge[F, Long]] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[SettableGauge[F, Long]](emptyMap[SettableGauge[F, Long]], instanceBuilder.gauge.settableLong(_, replaceExisting))
  }

  override def double(baseName: String, replaceExisting: Boolean = false): PerKeyMetric[SettableGauge[F, Double]] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[SettableGauge[F, Double]](
      emptyMap[SettableGauge[F, Double]],
      instanceBuilder.gauge.settableDouble(_, replaceExisting)
    )
  }

  override def forType[T](baseName: String, replaceExisting: Boolean = false)(gauge: () => T): PerKeyMetric[Gauge[F, T]] = {
    val instanceBuilder = monitor.named(baseName)
    new PerKeyMetricImpl[Gauge[F, T]](emptyMap[Gauge[F, T]], instanceBuilder.gauge.generic(_, replaceExisting)(gauge))
  }
}
