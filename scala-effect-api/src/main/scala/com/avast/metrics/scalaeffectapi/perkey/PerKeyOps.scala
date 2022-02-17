package com.avast.metrics.scalaeffectapi.perkey

import com.avast.metrics.scalaeffectapi._

trait PerKeyOps[F[_]] {
  def meter(name: String): PerKeyMetric[Meter[F]]
  def counter(name: String): PerKeyMetric[Counter[F]]
  def timer(name: String): PerKeyMetric[Timer[F]]
  def timerPair(name: String): PerKeyMetric[TimerPair[F]]
  def histogram(name: String): PerKeyMetric[Histogram[F]]
  def gauge: PerKeyGaugeFactory[F]
}
