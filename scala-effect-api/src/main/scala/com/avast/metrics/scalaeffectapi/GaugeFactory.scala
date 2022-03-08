package com.avast.metrics.scalaeffectapi

import cats.effect.std.Dispatcher

trait GaugeFactory[F[_]] {
  def settableLong(name: String, replaceExisting: Boolean = false): SettableGauge[F, Long]
  def settableDouble(name: String, replaceExisting: Boolean = false): SettableGauge[F, Double]
  def generic[T](name: String, replaceExisting: Boolean = false)(retrieveValue: () => T): Gauge[F, T]
  def genericWithUnsafeRun[T](name: String, replaceExisting: Boolean = false)(retrieveValue: F[T])(implicit
      dispatcher: Dispatcher[F]
  ): Gauge[F, T]
}
