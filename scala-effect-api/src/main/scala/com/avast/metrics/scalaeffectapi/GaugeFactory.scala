package com.avast.metrics.scalaeffectapi

import cats.effect.std.Dispatcher

trait GaugeFactory[F[_]] {
  def long(name: String, replaceExisting: Boolean = false): SettableGauge[F, Long]
  def double(name: String, replaceExisting: Boolean = false): SettableGauge[F, Double]
  def forType[T](name: String, replaceExisting: Boolean = false)(retrieveValue: () => T): Gauge[F, T]
  def forTypeWithUnsafeRun[T](name: String, replaceExisting: Boolean = false)(retrieveValue: F[T])(implicit
      dispatcher: Dispatcher[F]
  ): Gauge[F, T]
}
