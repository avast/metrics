package com.avast.metrics.scalaeffectapi.perkey

import com.avast.metrics.scalaeffectapi.{Gauge, SettableGauge}

trait PerKeyGaugeFactory[F[_]] {
  def settableLong(baseName: String, replaceExisting: Boolean = false): PerKeyMetric[SettableGauge[F, Long]]
  def settableDouble(baseName: String, replaceExisting: Boolean = false): PerKeyMetric[SettableGauge[F, Double]]
  def generic[T](baseName: String, replaceExisting: Boolean = false)(gauge: () => T): PerKeyMetric[Gauge[F, T]]
}
