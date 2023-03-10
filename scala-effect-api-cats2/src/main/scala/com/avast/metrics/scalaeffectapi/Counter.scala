package com.avast.metrics.scalaeffectapi

import com.avast.metrics.scalaapi.Metric

trait Counter[F[_]] extends Counting[F] with Metric {
  def inc: F[Unit]
  def inc(n: Long): F[Unit]
  def dec: F[Unit]
  def dec(n: Int): F[Unit]
}
