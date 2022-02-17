package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._

import java.time.Duration
import com.avast.metrics.scalaeffectapi.{Timer, TimerPair}

private class TimerPairImpl[F[_]: Sync](success: Timer[F], failure: Timer[F]) extends TimerPair[F] {
  override def start: F[TimerPairContext] = {
    for {
      succCtx <- success.start
      failCtx <- failure.start
    } yield new TimerPairContext {
      override def stop: F[Duration] = success.stop(succCtx)

      override def stopFailure: F[Duration] = failure.stop(failCtx)
    }
  }

  override def stop(context: TimerPairContext): F[Duration] = context.stop

  override def stopFailure(context: TimerPairContext): F[Duration] = context.stopFailure

}