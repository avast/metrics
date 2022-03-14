package com.avast.metrics.scalaeffectapi.perkey.impl

import cats.data.NonEmptyList
import com.avast.metrics.scalaeffectapi._
import com.avast.metrics.scalaeffectapi.perkey.{PerKeyGaugeFactory, PerKeyHelper, PerKeyMetric, PerKeyOps}

import scala.collection.concurrent.TrieMap

private[perkey] class PerKeyOpsImpl[F[_]](monitor: Monitor[F]) extends PerKeyOps[F] {
  private def emptyMap[M] = TrieMap.empty[String, M]

  private def metricBuilder[T]: (String, (Monitor[F], String) => T) => NonEmptyList[String] => T = PerKeyHelper.metricBuilder(monitor)

  override def meter(baseName: String): PerKeyMetric[Meter[F]] = {
    val instanceBuilder: (Monitor[F], String) => Meter[F] = (m, n) => m.meter(n)
    new PerKeyMetricImpl[Meter[F]](emptyMap[Meter[F]], metricBuilder(baseName, instanceBuilder))
  }

  override def counter(baseName: String): PerKeyMetric[Counter[F]] = {
    val instanceBuilder: (Monitor[F], String) => Counter[F] = (m, n) => m.counter(n)
    new PerKeyMetricImpl[Counter[F]](emptyMap, metricBuilder(baseName, instanceBuilder))
  }

  override def timer(baseName: String): PerKeyMetric[Timer[F]] = {
    val instanceBuilder: (Monitor[F], String) => Timer[F] = (m, n) => m.timer(n)
    new PerKeyMetricImpl[Timer[F]](emptyMap, metricBuilder(baseName, instanceBuilder))
  }

  override def timerPair(baseName: String): PerKeyMetric[TimerPair[F]] = {
    val instanceBuilder: (Monitor[F], String) => TimerPair[F] = (m, n) => m.timerPair(n)
    new PerKeyMetricImpl[TimerPair[F]](emptyMap, metricBuilder(baseName, instanceBuilder))
  }

  override def histogram(baseName: String): PerKeyMetric[Histogram[F]] = {
    val instanceBuilder: (Monitor[F], String) => Histogram[F] = (m, n) => m.histogram(n)
    new PerKeyMetricImpl[Histogram[F]](emptyMap, metricBuilder(baseName, instanceBuilder))
  }

  override def gauge: PerKeyGaugeFactory[F] = {
    new PerKeyGaugeFactoryImpl[F](monitor)
  }
}
