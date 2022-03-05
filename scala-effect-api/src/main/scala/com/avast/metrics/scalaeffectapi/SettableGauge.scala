package com.avast.metrics.scalaeffectapi

trait SettableGauge[F[_], T] extends Gauge[F, T] {
  def set(value: T): F[Unit]
}
