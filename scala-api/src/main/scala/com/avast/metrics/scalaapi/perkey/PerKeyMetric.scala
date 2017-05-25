package com.avast.metrics.scalaapi.perkey

trait PerKeyMetric[M] {
  def forKey(str: String): M
}
