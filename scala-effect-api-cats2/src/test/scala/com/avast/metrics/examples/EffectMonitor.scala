package com.avast.metrics.examples

import cats.effect.{ExitCode, IO, IOApp, Timer}
import com.avast.metrics.dropwizard.JmxMetricsMonitor
import com.avast.metrics.scalaeffectapi.Monitor

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

object EffectMonitor extends IOApp {

  val jmxMetricsMonitor = new JmxMetricsMonitor("com.avast.some.app")

  override def run(args: List[String]): IO[ExitCode] = {
    val monitor = Monitor.wrapJava[IO](jmxMetricsMonitor)
    val counter = monitor.counter("counter")
    val timer = monitor.timer("timer")

    for {
      _ <- counter.inc
      _ <- timer.time {
        Timer[IO](IO.timer(executionContext)).sleep(FiniteDuration(500, TimeUnit.MILLISECONDS))
      }
    } yield ExitCode.Success
  }
}
