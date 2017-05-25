package com.avast.metrics.scalaapi

package object perkey {
  implicit class MonitorToPerKeyOps(monitor: Monitor) {
    def perKey: PerKeyOps = PerKeyMonitor(monitor).perKey
  }
}
