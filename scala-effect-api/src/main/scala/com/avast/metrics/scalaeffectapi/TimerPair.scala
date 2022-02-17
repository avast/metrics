package com.avast.metrics.scalaeffectapi

import cats.effect.{ExitCase, Resource, Sync}
import cats.syntax.functor._

import java.time.Duration

trait TimerPair[F[_]] {
  type Context
  def start: F[Context]
  def stop(context: Context): F[Duration]
  def stopFailure(context: Context): F[Duration]
}

object TimerPair {
  object Dsl {
    implicit class TimerPairOps[F[_]](val timerPair: TimerPair[F]) extends AnyVal {
      def time[T](action: F[T])(implicit sync: Sync[F]): F[T] = {
        Resource
          .makeCase(timerPair.start) {
            case (ctx, ExitCase.Completed) => timerPair.stop(ctx).as(())
            case (ctx, _) => timerPair.stopFailure(ctx).as(())
          }
      }.use(_ => action)
    }
  }
}
