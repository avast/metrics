package com.avast.metrics.scalaeffectapi

package object perkey {
  implicit class MonitorToPerKeyOps[F[_]](monitor: Monitor[F]) {
    def perKey: PerKeyOps[F] = PerKeyMonitor(monitor).perKey
  }
}
