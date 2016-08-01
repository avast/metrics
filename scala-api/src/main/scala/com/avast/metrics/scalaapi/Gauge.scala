package com.avast.metrics.scalaapi

trait Gauge[T] extends Metric {
  def value: T
}
