package com.avast.metrics.scalaeffectapi

import com.avast.metrics.scalaapi.Metric

trait Gauge[F[_], T] extends Metric {
  def value: F[T]
  def set(value: T): F[Unit]
}
