package com.avast.metrics.scalaeffectapi

import com.avast.metrics.api.Timer.TimeContext

import java.time.{Duration => JDuration}
import scala.concurrent.duration.Duration

trait Timer[F[_]] {
  def start: F[TimeContext]
  def stop(context: TimeContext): F[Duration]
  def update(duration: JDuration): F[Unit]
  def update(duration: Duration): F[Unit]
  def time[A](block: F[A]): F[A]
}
