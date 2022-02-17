package com.avast.metrics.scalaeffectapi

import java.time.Duration

trait TimerPairContext[F[_]] {
  def stop: F[Duration]
  def stopFailure: F[Duration]
}
