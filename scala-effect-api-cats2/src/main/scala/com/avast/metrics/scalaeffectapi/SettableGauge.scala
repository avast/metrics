package com.avast.metrics.scalaeffectapi

trait SettableGauge[F[_], T] extends Gauge[F, T] {
  def set(value: T): F[Unit]
  def update(f: T => T): F[T]
  def inc: F[T]
  def dec: F[T]
}
