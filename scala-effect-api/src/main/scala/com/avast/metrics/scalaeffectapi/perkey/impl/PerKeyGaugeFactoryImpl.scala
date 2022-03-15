package com.avast.metrics.scalaeffectapi.perkey.impl

import cats.effect.std.Dispatcher
import com.avast.metrics.scalaeffectapi.perkey.PerKeyHelper.MetricBuilder
import com.avast.metrics.scalaeffectapi.{Gauge, Monitor, SettableGauge}
import com.avast.metrics.scalaeffectapi.perkey.{PerKeyGaugeFactory, PerKeyHelper, PerKeyMetric}

import scala.collection.concurrent.TrieMap

class PerKeyGaugeFactoryImpl[F[_]](monitor: Monitor[F]) extends PerKeyGaugeFactory[F] {
  private def emptyMap[M] = TrieMap.empty[String, M]

  private def metricBuilder[T]: (String, (Monitor[F], String) => T) => MetricBuilder[T] = PerKeyHelper.metricBuilder(monitor)

  override def settableLong(baseName: String, replaceExisting: Boolean = false): PerKeyMetric[SettableGauge[F, Long]] = {
    val instanceBuilder: (Monitor[F], String) => SettableGauge[F, Long] = (m, n) => m.gauge.settableLong(n, replaceExisting)
    new PerKeyMetricImpl[SettableGauge[F, Long]](emptyMap[SettableGauge[F, Long]], metricBuilder(baseName, instanceBuilder))
  }

  override def settableDouble(baseName: String, replaceExisting: Boolean = false): PerKeyMetric[SettableGauge[F, Double]] = {
    val instanceBuilder: (Monitor[F], String) => SettableGauge[F, Double] = (m, n) => m.gauge.settableDouble(n, replaceExisting)
    new PerKeyMetricImpl[SettableGauge[F, Double]](
      emptyMap[SettableGauge[F, Double]],
      metricBuilder(baseName, instanceBuilder)
    )
  }

  override def generic[T](baseName: String, replaceExisting: Boolean = false)(retrieveValue: () => T): PerKeyMetric[Gauge[F, T]] = {
    val instanceBuilder: (Monitor[F], String) => Gauge[F, T] = (m, n) => m.gauge.generic(n, replaceExisting)(retrieveValue)
    new PerKeyMetricImpl[Gauge[F, T]](emptyMap[Gauge[F, T]], metricBuilder(baseName, instanceBuilder))
  }

  override def genericWithUnsafeRun[T](baseName: String, replaceExisting: Boolean = false)(retrieveValue: F[T])(implicit
      dispatcher: Dispatcher[F]
  ): PerKeyMetric[Gauge[F, T]] = {
    val instanceBuilder: (Monitor[F], String) => Gauge[F, T] = (m, n) => m.gauge.genericWithUnsafeRun(n, replaceExisting)(retrieveValue)
    new PerKeyMetricImpl[Gauge[F, T]](emptyMap[Gauge[F, T]], metricBuilder(baseName, instanceBuilder))
  }
}
