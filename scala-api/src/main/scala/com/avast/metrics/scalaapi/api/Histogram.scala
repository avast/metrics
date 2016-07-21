package com.avast.metrics.scalaapi.api

trait Histogram extends Metric {
  def update(value: Long): Unit
}
