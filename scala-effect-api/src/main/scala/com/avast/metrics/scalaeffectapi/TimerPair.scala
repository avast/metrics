package com.avast.metrics.scalaeffectapi

import java.time.Duration

trait TimerPair[F[_]] {
  trait TimerPairContext {
    def stop: F[Duration]
    def stopFailure: F[Duration]
  }

  def start: F[TimerPairContext]
  def update(duration: Duration): F[Unit]
  def updateFailure(duration: Duration): F[Unit]
  def time[T](action: F[T]): F[T]
}
