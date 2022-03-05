package com.avast.metrics.scalaeffectapi

trait GaugeFactory[F[_]] {
  def long(name: String, replaceExisting: Boolean = false): SettableGauge[F, Long]
  def double(name: String, replaceExisting: Boolean = false): SettableGauge[F, Double]
  def forType[T](name: String, replaceExisting: Boolean = false)(gauge: () => T): Gauge[F, T]
}
