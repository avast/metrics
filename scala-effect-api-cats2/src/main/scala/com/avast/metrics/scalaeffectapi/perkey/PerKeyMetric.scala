package com.avast.metrics.scalaeffectapi.perkey

trait PerKeyMetric[M] {
  def forKey(str: String): M
}
