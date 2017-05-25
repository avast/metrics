package com.avast.metrics.examples

import com.avast.metrics.dropwizard.JmxMetricsMonitor
import com.avast.metrics.scalaapi.Monitor

import scala.concurrent.Future
// never use implicit ExecutionContext in production
import scala.concurrent.ExecutionContext.Implicits.global

object PerKeyExample extends App {

  import com.avast.metrics.scalaapi.perkey._

  val monitor = Monitor(new JmxMetricsMonitor("com.avast.some.app"))

  val requestsCounter = monitor.perKey.meter("requests")
  val perPartnerRequestCounter = monitor.perKey.counter("requestPerVendor")

  val x = perPartnerRequestCounter.forKey("a")
  val a = perPartnerRequestCounter.forKey("b")

  println(x.name, a.name)

  val perPartnerRequestTimer = monitor.perKey.timerPair("requests")
  perPartnerRequestTimer.forKey("c").time(Future.successful("meh"))

  while (true) {
    Thread.sleep(10000)
  }
}
