package com.avast.metrics.scalaeffectapi.perkey

import cats.effect.std.Dispatcher
import com.avast.metrics.scalaeffectapi.{Gauge, SettableGauge}

trait PerKeyGaugeFactory[F[_]] {
  def long(baseName: String, replaceExisting: Boolean = false): PerKeyMetric[SettableGauge[F, Long]]
  def double(baseName: String, replaceExisting: Boolean = false): PerKeyMetric[SettableGauge[F, Double]]
  def forType[T](baseName: String, replaceExisting: Boolean = false)(retrieveValue: () => T): PerKeyMetric[Gauge[F, T]]
  def forTypeWithUnsafeRun[T](baseName: String, replaceExisting: Boolean = false)(retrieveValue: F[T])(implicit
      dispatcher: Dispatcher[F]
  ): PerKeyMetric[Gauge[F, T]]
}
