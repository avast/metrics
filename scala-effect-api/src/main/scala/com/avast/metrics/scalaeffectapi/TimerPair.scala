package com.avast.metrics.scalaeffectapi

import cats.effect.{ExitCase, Resource, Sync}
import cats.syntax.functor._

import java.time.Duration

trait TimerPair[F[_]] {
  trait TimerPairContext {
    def stop: F[Duration]
    def stopFailure: F[Duration]
  }

  def start: F[TimerPairContext]
  def stop(context: TimerPairContext): F[Duration]
  def stopFailure(context: TimerPairContext): F[Duration]
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
