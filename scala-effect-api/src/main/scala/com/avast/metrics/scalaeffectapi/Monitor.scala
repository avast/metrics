package com.avast.metrics.scalaeffectapi

import cats.effect.Sync
import cats.effect.std.Dispatcher
import com.avast.metrics.api.{Monitor => JMonitor, Naming}
import com.avast.metrics.scalaapi.{Monitor => SMonitor}
import com.avast.metrics.scalaeffectapi.impl.AutoInitMonitorImpl
import com.avast.metrics.test.NoOpMonitor

trait Monitor[F[_]] extends AutoCloseable {
  def named(name: String): Monitor[F]
  def named(name1: String, name2: String, restOfNames: String*): Monitor[F]
  def getName: String
  def meter(name: String): Meter[F]
  def counter(name: String): Counter[F]
  def timer(name: String): Timer[F]
  def timerPair(name: String): TimerPair[F]
  def histogram(name: String): Histogram[F]

  def gauge: GaugeFactory[F]

  def asJava: JMonitor
  def asPlainScala: SMonitor
}

object Monitor {

  def wrapJava[F[_]: Sync](monitor: JMonitor): Monitor[F] = wrapJava(monitor, Naming.defaultNaming())
  def wrapJava[F[_]: Sync](monitor: JMonitor, naming: Naming): Monitor[F] = wrap(SMonitor(monitor), naming)

  def wrap[F[_]: Sync](monitor: SMonitor): Monitor[F] = wrap(monitor, Naming.defaultNaming())
  def wrap[F[_]: Sync](monitor: SMonitor, naming: Naming): Monitor[F] = new impl.MonitorImpl(monitor, naming)

  def wrapWithAutoInit[F[_]: Sync](monitor: SMonitor)(implicit dispatcher: Dispatcher[F]): Monitor[F] =
    wrapWithAutoInit(monitor, Naming.defaultNaming())
  def wrapWithAutoInit[F[_]: Sync](monitor: SMonitor, naming: Naming)(implicit dispatcher: Dispatcher[F]): Monitor[F] =
    new AutoInitMonitorImpl[F](monitor, naming)

  def noOp[F[_]: Sync](): Monitor[F] = {
    wrapJava(NoOpMonitor.INSTANCE)
  }

}
