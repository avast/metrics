package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import com.avast.metrics.api.Timer.TimeContext

import java.time.Duration
import com.avast.metrics.scalaapi.{Timer => STimer}
import com.avast.metrics.scalaeffectapi.Timer

private class TimerImpl[F[_]: Sync](inner: STimer) extends Timer[F] {
  override type Context = TimeContext

  override def start: F[Context] = Sync[F].delay(inner.start())

  override def stop(context: Context): F[Duration] = Sync[F].delay(Duration.ofNanos(context.stopAndGetTime()))
}
