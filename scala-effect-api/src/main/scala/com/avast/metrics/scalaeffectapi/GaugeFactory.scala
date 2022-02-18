package com.avast.metrics.scalaeffectapi

trait GaugeFactory[F[_]] {
  def long(name: String): Gauge[F, Long]
  def double(name: String): Gauge[F, Double]
}
