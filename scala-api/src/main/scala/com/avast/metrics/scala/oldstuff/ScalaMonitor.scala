package com.avast.metrics.scala.oldstuff

import java.util.function.Supplier

import com.avast.metrics.api._

import scala.concurrent.ExecutionContext

class ScalaMonitor(monitor: Monitor)(implicit ec: ExecutionContext) extends Monitor {
  override def named(name: String): Monitor = monitor.named(name)

  override def named(name1: String, name2: String, restOfNames: String*): Monitor = monitor.named(name1, name2, restOfNames: _*)

  override def getName: String = monitor.getName

  override def newMeter(name: String): Meter = monitor.newMeter(name)

  override def newCounter(name: String): Counter = monitor.newCounter(name)

  override def newTimer(name: String): Timer = monitor.newTimer(name)

  override def newGauge[T](name: String, gauge: Supplier[T]): Gauge[T] = monitor.newGauge(name, gauge)

  override def newHistogram(name: String): Histogram = monitor.newHistogram(name)

  override def remove(metric: Metric): Unit = monitor.remove(metric)

  override def close(): Unit = monitor.close()

  def blockTimer[A](baseName: String): BlockTimer[A] =
    new BlockTimer[A](newTimer(succName(baseName)), newTimer(failName(baseName)), new BlockResultObserver[A])

  def futureTimer[A](baseName: String): FutureTimer[A] =
    new FutureTimer[A](newTimer(succName(baseName)), newTimer(failName(baseName)), new FutureResultObserver[A])

  def blockCounter[A](baseName: String): BlockCounters[A] =
    new BlockCounters[A](newCounter(succName(baseName)), newCounter(failName(baseName)), new BlockResultObserver[A])

  def futureCounter[A](baseName: String): FutureCounters[A] =
    new FutureCounters[A](newCounter(succName(baseName)), newCounter(failName(baseName)), new FutureResultObserver[A])

  private def succName(base: String) = s"$base-success"
  private def failName(base: String) = s"$base-failure"
}
