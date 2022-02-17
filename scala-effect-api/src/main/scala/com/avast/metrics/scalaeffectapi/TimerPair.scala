package com.avast.metrics.scalaeffectapi

import java.time.Duration

trait TimerPair[F[_]] {
  trait TimerPairContext {
    def stop: F[Duration]
    def stopFailure: F[Duration]
  }

  def start: F[TimerPairContext]
  def stop(context: TimerPairContext): F[Duration]
  def stopFailure(context: TimerPairContext): F[Duration]
  def time[T](action: F[T]): F[T]
}
