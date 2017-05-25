package com.avast.metrics.scalaapi.perkey

import com.avast.metrics.scalaapi._

trait PerKeyOps {
  def meter(name: String): PerKeyMetric[Meter]
  def counter(name: String): PerKeyMetric[Counter]
  def timer(name: String): PerKeyMetric[Timer]
  def timerPair(name: String): PerKeyMetric[TimerPair]
  def gauge[A](name: String)(gauge: () => A): PerKeyMetric[Gauge[A]]
  def gauge[A](name: String, replaceExisting: Boolean)(gauge: () => A): PerKeyMetric[Gauge[A]]
  def histogram(name: String): PerKeyMetric[Histogram]

}
