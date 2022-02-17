package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.avast.metrics.api.Timer.TimeContext

import java.time.{Duration => JDuration}
import com.avast.metrics.scalaapi.{Timer => STimer}
import com.avast.metrics.scalaeffectapi.Timer

import scala.concurrent.duration.Duration

private class TimerImpl[F[_]: Sync](inner: STimer) extends Timer[F] {
  override def start: F[TimeContext] = Sync[F].delay(inner.start())

  override def stop(context: TimeContext): F[Duration] = Sync[F].delay(Duration.fromNanos(context.stopAndGetTime()))

  override def update(duration: JDuration): F[Unit] = Sync[F].delay(inner.update(duration))

  override def update(duration: Duration): F[Unit] = update(JDuration.ofNanos(duration.toNanos))

  override def time[A](block: F[A]): F[A] = {
    for {
      context <- start
      result <- block
      _ <- stop(context)
    } yield result
  }
}
