package com.avast.metrics.scalaeffectapi.perkey

import com.avast.metrics.scalaeffectapi.{Gauge, SettableGauge}

trait PerKeyGaugeFactory[F[_]] {
  def long(baseName: String, replaceExisting: Boolean = false): PerKeyMetric[SettableGauge[F, Long]]
  def double(baseName: String, replaceExisting: Boolean = false): PerKeyMetric[SettableGauge[F, Double]]
  def forType[T](baseName: String, replaceExisting: Boolean = false)(gauge: () => T): PerKeyMetric[Gauge[F, T]]
}
