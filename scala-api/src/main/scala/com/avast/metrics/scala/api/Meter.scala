package com.avast.metrics.scala.api

trait Meter extends Counting {
  def mark: Unit
  def mark(n: Long): Unit
}
