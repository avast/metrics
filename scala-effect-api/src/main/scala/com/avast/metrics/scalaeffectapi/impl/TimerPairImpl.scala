package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._

import java.time.Duration
import com.avast.metrics.scalaeffectapi.{Timer, TimerPair, TimerPairContext}

private class TimerPairImpl[F[_]: Sync](success: Timer[F], failure: Timer[F]) extends TimerPair[F] {
  override type Context = TimerPairContext[F]

  override def start: F[Context] = {
    for {
      succCtx <- success.start
      failCtx <- failure.start
    } yield new Context {
      override def stop: F[Duration] = success.stop(succCtx)

      override def stopFailure: F[Duration] = failure.stop(failCtx)
    }
  }

  override def stop(context: Context): F[Duration] = context.stop

  override def stopFailure(context: Context): F[Duration] = context.stopFailure

}
