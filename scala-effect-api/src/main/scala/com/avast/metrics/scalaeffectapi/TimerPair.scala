package com.avast.metrics.scalaeffectapi

import java.time.{Duration => JDuration}
import scala.concurrent.duration.Duration

trait TimerPair[F[_]] {
  trait TimerPairContext extends AutoCloseable {
    def stop: F[Duration]
    def stopFailure: F[Duration]
  }

  def start: F[TimerPairContext]
  def update(duration: JDuration): F[Unit]
  def updateFailure(duration: JDuration): F[Unit]
  def update(duration: Duration): F[Unit]
  def updateFailure(duration: Duration): F[Unit]
  def time[T](action: F[T]): F[T]
  def time[T](action: F[T])(successCheck: T => Boolean): F[T]
  def time[T](action: F[T])(successCheck: T => Boolean)(successCallback: Duration => F[Unit], failureCallback: Duration => F[Unit]): F[T]
}
