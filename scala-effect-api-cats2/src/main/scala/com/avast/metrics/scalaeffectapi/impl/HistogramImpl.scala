package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import com.avast.metrics.scalaapi.{Histogram => SHistogram}
import com.avast.metrics.scalaeffectapi.Histogram

private class HistogramImpl[F[_]: Sync](histogram: SHistogram) extends Histogram[F] {
  override def update(value: Long): F[Unit] = Sync[F].delay(histogram.update(value))

  override def name: String = histogram.name
}
