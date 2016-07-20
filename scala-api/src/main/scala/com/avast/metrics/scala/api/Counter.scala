package com.avast.metrics.scala.api

trait Counter extends Metric with Counting {
  def inc(): Unit
  def inc(n: Long): Unit
  def dec(): Unit
  def dec(n: Int): Unit
}
