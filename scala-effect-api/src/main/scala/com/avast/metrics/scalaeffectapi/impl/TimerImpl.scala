package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.avast.metrics.api.Timer.TimeContext

import java.time.Duration
import com.avast.metrics.scalaapi.{Timer => STimer}
import com.avast.metrics.scalaeffectapi.Timer

private class TimerImpl[F[_]: Sync](inner: STimer) extends Timer[F] {
  override def start: F[TimeContext] = Sync[F].delay(inner.start())

  override def stop(context: TimeContext): F[Duration] = Sync[F].delay(Duration.ofNanos(context.stopAndGetTime()))

  override def time[A](block: F[A]): F[A] = {
    for {
      context <- start
      result <- block
      _ <- stop(context)
    } yield result
  }
}
