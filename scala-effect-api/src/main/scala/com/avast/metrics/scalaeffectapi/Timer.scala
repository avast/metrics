package com.avast.metrics.scalaeffectapi

import cats.Monad
import cats.syntax.flatMap._
import cats.syntax.functor._
import java.time.Duration

trait Timer[F[_]] {
  type Context
  def start: F[Context]
  def stop(context: Context): F[Duration]
}

object Timer {
  object Dsl {
    implicit class TimerOps[F[_]](val timer: Timer[F]) extends AnyVal {
      def time[A](block: F[A])(implicit monad: Monad[F]): F[A] = {
        for {
          context <- timer.start
          result <- block
          _ <- timer.stop(context)
        } yield result
      }
    }
  }
}
