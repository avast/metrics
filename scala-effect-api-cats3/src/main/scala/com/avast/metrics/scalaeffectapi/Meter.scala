package com.avast.metrics.scalaeffectapi

import com.avast.metrics.scalaapi.Metric

trait Meter[F[_]] extends Counting[F] with Metric {
  def mark: F[Unit]
  def mark(n: Long): F[Unit]
}
