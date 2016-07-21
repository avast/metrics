package com.avast.metrics.scalaapi

import com.avast.metrics.api.{Histogram => JHistogram}

class Histogram(histogram: JHistogram) extends api.Histogram {
  override def update(value: Long): Unit = histogram.update(value)

  override def name: String = histogram.getName
}
