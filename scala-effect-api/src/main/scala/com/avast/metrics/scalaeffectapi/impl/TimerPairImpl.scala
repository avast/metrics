package com.avast.metrics.scalaeffectapi.impl

import cats.effect.{Resource, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._

import java.time.{Duration => JDuration}
import com.avast.metrics.scalaeffectapi.{Timer, TimerPair}

import scala.concurrent.duration.Duration

private class TimerPairImpl[F[_]: Sync](success: Timer[F], failure: Timer[F]) extends TimerPair[F] {
  override def start: F[TimerPairContext] = {
    for {
      succCtx <- success.start
      failCtx <- failure.start
    } yield new TimerPairContext {
      override def stop: F[Duration] = succCtx.stop

      override def stopFailure: F[Duration] = failCtx.stop

      override def close(): Unit = {
        succCtx.close()
        failCtx.close()
      }
    }
  }

  override def update(duration: JDuration): F[Unit] = Sync[F].delay(success.update(duration))

  override def updateFailure(duration: JDuration): F[Unit] = Sync[F].delay(failure.update(duration))

  override def update(duration: Duration): F[Unit] = Sync[F].delay(success.update(duration))

  override def updateFailure(duration: Duration): F[Unit] = Sync[F].delay(failure.update(duration))

  override def time[T](action: F[T]): F[T] = {
    Resource
      .makeCase(start) {
        case (ctx, Resource.ExitCase.Succeeded) => ctx.stop.as(())
        case (ctx, _) => ctx.stopFailure.as(())
      }
  }.use(_ => action)
}
