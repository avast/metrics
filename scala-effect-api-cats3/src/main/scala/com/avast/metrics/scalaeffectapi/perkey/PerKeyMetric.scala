package com.avast.metrics.scalaeffectapi.perkey

trait PerKeyMetric[M] {
  def forKey(str: String): M
  def forKeys(name: String, name2: String, names: String*): M
}
