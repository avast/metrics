package com.avast.metrics.scalaeffectapi

import com.avast.metrics.scalaapi.Metric

trait Histogram[F[_]] extends Metric {
  def update(value: Long): F[Unit]
}
