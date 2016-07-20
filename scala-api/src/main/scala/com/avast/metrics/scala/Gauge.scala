package com.avast.metrics.scala

import com.avast.metrics.api.{Gauge => JGauge}
import com.avast.metrics.scala.api.{Gauge => IGauge}

class Gauge[A](gauge: JGauge[A]) extends IGauge[A] {
  override def value: A = gauge.getValue

  override def name: String = gauge.getName
}
