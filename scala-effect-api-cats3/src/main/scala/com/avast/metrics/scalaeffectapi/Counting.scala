package com.avast.metrics.scalaeffectapi

trait Counting[F[_]] {
  def count: F[Long]
}
