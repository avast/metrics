package com.avast.metrics.scalaeffectapi

import java.time.{Duration => JDuration}
import scala.concurrent.duration.Duration

trait Timer[F[_]] {
  trait TimeContext extends AutoCloseable {
    def stop: F[Duration]
  }

  def start: F[TimeContext]
  def update(duration: JDuration): F[Unit]
  def update(duration: Duration): F[Unit]
  def time[A](block: F[A]): F[A]
  def time[A](block: F[A])(callback: Duration => F[Unit]): F[A]
}
