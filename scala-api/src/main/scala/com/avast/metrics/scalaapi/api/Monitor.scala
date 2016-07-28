package com.avast.metrics.scalaapi.api

trait Monitor {
  def named(name: String): Monitor
  def named(name: String, name2: String, names: String*): Monitor
  def getName: String
  def meter(name: String): Meter
  def counter(name: String): Counter
  def timer(name: String): Timer
  def timerPair(name: String): TimerPair
  def gauge[A](name: String)(gauge: () => A): Gauge[A]
  def gauge[A](name: String, replaceExisting: Boolean)(gauge: () => A): Gauge[A]
  def histogram(name: String): Histogram
}
