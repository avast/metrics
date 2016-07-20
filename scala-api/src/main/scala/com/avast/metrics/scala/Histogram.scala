package com.avast.metrics.scala

import com.avast.metrics.api.{Histogram => JHistogram}
import com.avast.metrics.scala.api.{Histogram => IHistogram}

class Histogram(histogram: JHistogram) extends IHistogram {
  override def update(value: Long): Unit = histogram.update(value)

  override def name: String = histogram.getName
}
