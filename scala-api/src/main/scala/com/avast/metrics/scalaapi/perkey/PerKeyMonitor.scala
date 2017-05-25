package com.avast.metrics.scalaapi.perkey

import com.avast.metrics.scalaapi.perkey.impl.{PerKeyMonitorImpl, PerKeyOpsImpl}

trait PerKeyMonitor extends com.avast.metrics.scalaapi.Monitor {
  def perKey: PerKeyOps
}

object PerKeyMonitor {
  def apply(monitor: com.avast.metrics.scalaapi.Monitor): PerKeyMonitor =
    new PerKeyMonitorImpl(monitor, new PerKeyOpsImpl(monitor))
}
