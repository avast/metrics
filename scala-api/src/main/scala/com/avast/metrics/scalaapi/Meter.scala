package com.avast.metrics.scalaapi

trait Meter extends Counting with Metric {
  def mark(): Unit
  def mark(n: Long): Unit
}
