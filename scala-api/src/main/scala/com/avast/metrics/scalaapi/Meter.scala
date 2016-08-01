package com.avast.metrics.scalaapi

trait Meter extends Counting {
  def mark(): Unit
  def mark(n: Long): Unit
}
