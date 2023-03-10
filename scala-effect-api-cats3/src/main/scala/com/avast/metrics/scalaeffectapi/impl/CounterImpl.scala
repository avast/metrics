package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import com.avast.metrics.scalaapi.{Counter => SCounter}
import com.avast.metrics.scalaeffectapi.Counter

private class CounterImpl[F[_]: Sync](inner: SCounter) extends Counter[F] {
  override def inc: F[Unit] = Sync[F].delay(inner.inc())

  override def inc(n: Long): F[Unit] = Sync[F].delay(inner.inc(n))

  override def dec: F[Unit] = Sync[F].delay(inner.dec())

  override def dec(n: Int): F[Unit] = Sync[F].delay(inner.dec(n))

  override def count: F[Long] = Sync[F].delay(inner.count)

  override def name: String = inner.name
}
