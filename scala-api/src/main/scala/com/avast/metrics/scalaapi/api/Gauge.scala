package com.avast.metrics.scalaapi.api

trait Gauge[T] extends Metric {
  def value: T
}
