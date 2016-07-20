package com.avast.metrics.scala.api

trait Histogram extends Metric {
  def update(value: Long): Unit
}
