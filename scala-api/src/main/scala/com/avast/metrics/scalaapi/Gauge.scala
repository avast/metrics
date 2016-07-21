package com.avast.metrics.scalaapi

import com.avast.metrics.api.{Gauge => JGauge}

class Gauge[A](gauge: JGauge[A]) extends api.Gauge[A] {
  override def value: A = gauge.getValue

  override def name: String = gauge.getName
}
