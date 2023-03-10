package com.avast.metrics.scalaeffectapi

trait GaugeFactory[F[_]] {
  def settableLong(name: String, replaceExisting: Boolean = false): SettableGauge[F, Long]
  def settableDouble(name: String, replaceExisting: Boolean = false): SettableGauge[F, Double]
  def generic[T](name: String, replaceExisting: Boolean = false)(gauge: () => T): Gauge[F, T]
}
