package com.avast.metrics.scalaapi

trait Histogram extends Metric {
  def update(value: Long): Unit
}
