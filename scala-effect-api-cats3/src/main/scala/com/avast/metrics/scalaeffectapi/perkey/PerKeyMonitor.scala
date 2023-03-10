package com.avast.metrics.scalaeffectapi.perkey

import com.avast.metrics.scalaeffectapi.Monitor
import com.avast.metrics.scalaeffectapi.perkey.impl.{PerKeyMonitorImpl, PerKeyOpsImpl}

trait PerKeyMonitor[F[_]] extends Monitor[F] {
  def perKey: PerKeyOps[F]
}

object PerKeyMonitor {
  def apply[F[_]](monitor: Monitor[F]): PerKeyMonitor[F] =
    new PerKeyMonitorImpl(monitor, new PerKeyOpsImpl(monitor))
}
