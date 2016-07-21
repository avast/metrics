package com.avast.metrics.scalaapi

import com.avast.metrics.api.{Meter => JMeter}

class Meter(meter: JMeter) extends api.Meter {
  override def mark(): Unit = meter.mark()

  override def mark(n: Long): Unit = meter.mark(n)

  override def count: Long = meter.count()
}
