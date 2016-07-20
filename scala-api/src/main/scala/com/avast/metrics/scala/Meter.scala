package com.avast.metrics.scala

import com.avast.metrics.api.{Meter => JMeter}
import com.avast.metrics.scala.api.{Meter => IMeter}

class Meter(meter: JMeter) extends IMeter {
  override def mark(): Unit = meter.mark()

  override def mark(n: Long): Unit = meter.mark(n)

  override def count: Long = meter.count()
}
