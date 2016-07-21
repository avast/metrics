package com.avast.metrics.scalaapi.api

trait Meter extends Counting {
  def mark(): Unit
  def mark(n: Long): Unit
}
