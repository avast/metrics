package com.avast.metrics.scalaapi.impl

import com.avast.metrics.api.{Histogram => JHistogram}
import com.avast.metrics.scalaapi.Histogram

private class HistogramImpl(histogram: JHistogram) extends Histogram {
  override def update(value: Long): Unit = histogram.update(value)

  override def name: String = histogram.getName
}
