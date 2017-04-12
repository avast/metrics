package com.avast.metrics.scalaapi.impl

import com.avast.metrics.api.{Gauge => JGauge}
import com.avast.metrics.scalaapi.Gauge

private class GaugeImpl[A](gauge: JGauge[A]) extends Gauge[A] {
  override def value: A = gauge.getValue

  override def name: String = gauge.getName
}
