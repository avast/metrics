package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import com.avast.metrics.scalaapi.{Meter => SMeter}
import com.avast.metrics.scalaeffectapi.Meter

private class MeterImpl[F[_]: Sync](meter: SMeter) extends Meter[F] {
  override def mark: F[Unit] = Sync[F].delay(meter.mark())

  override def mark(n: Long): F[Unit] = Sync[F].delay(meter.mark(n))

  override def count: F[Long] = Sync[F].delay(meter.count)

  override def name: String = meter.name
}
