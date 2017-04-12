package com.avast.metrics.scalaapi.impl

import com.avast.metrics.api.{Meter => JMeter}
import com.avast.metrics.scalaapi.Meter

private class MeterImpl(meter: JMeter) extends Meter {
  override def mark(): Unit = meter.mark()

  override def mark(n: Long): Unit = meter.mark(n)

  override def count: Long = meter.count()
}
