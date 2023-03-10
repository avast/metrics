package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.avast.metrics.scalaeffectapi.{Timer, TimerPair}

import java.time.{Duration => JDuration}
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

  override def update(duration: JDuration): F[Unit] = success.update(duration)

  override def updateFailure(duration: JDuration): F[Unit] = failure.update(duration)

  override def update(duration: Duration): F[Unit] = success.update(duration)

  override def updateFailure(duration: Duration): F[Unit] = failure.update(duration)

  override def time[T](action: F[T]): F[T] = {
    for {
      ctx <- start
      result <- action.onError { case _ => ctx.stopFailure.void }
      _ <- ctx.stop
    } yield result
  }

  override def time[T](action: F[T])(successCheck: T => Boolean): F[T] = {
    for {
      ctx <- start
      result <- action.onError { case _ => ctx.stopFailure.void }
      _ <- if (successCheck(result)) ctx.stop else ctx.stopFailure
    } yield result
  }
}
