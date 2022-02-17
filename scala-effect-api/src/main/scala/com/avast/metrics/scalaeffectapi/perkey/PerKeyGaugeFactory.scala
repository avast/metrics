package com.avast.metrics.scalaeffectapi.perkey

import com.avast.metrics.scalaeffectapi.Gauge

trait PerKeyGaugeFactory[F[_]] {
  def long(baseName: String): PerKeyMetric[Gauge[F, Long]]
  def double(baseName: String): PerKeyMetric[Gauge[F, Double]]
}
