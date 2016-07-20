package com.avast.metrics.scala.api

trait Gauge[T] extends Metric {
  def value: T
}
