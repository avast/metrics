package com.avast.metrics.scalaeffectapi

import cats.effect.Sync
import com.avast.metrics.api.{Monitor => JMonitor, Naming}
import com.avast.metrics.scalaapi.{Monitor => SMonitor}
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
  def unwrap: SMonitor
}

object Monitor {

  def wrapJava[F[_]: Sync](monitor: JMonitor): F[Monitor[F]] = Sync[F].delay(wrapJavaUnsafe(monitor))
  def wrapJava[F[_]: Sync](monitor: JMonitor, naming: Naming): F[Monitor[F]] = Sync[F].delay(wrapJavaUnsafe(monitor, naming))

  def wrap[F[_]: Sync](monitor: SMonitor): F[Monitor[F]] = Sync[F].delay(wrapUnsafe(monitor))
  def wrap[F[_]: Sync](monitor: SMonitor, naming: Naming): F[Monitor[F]] = Sync[F].delay(wrapUnsafe(monitor, naming))

  def wrapJavaUnsafe[F[_]: Sync](monitor: JMonitor): Monitor[F] = wrapJavaUnsafe(monitor, Naming.defaultNaming())
  def wrapJavaUnsafe[F[_]: Sync](monitor: JMonitor, naming: Naming): Monitor[F] = wrapUnsafe(SMonitor(monitor), naming)

  def wrapUnsafe[F[_]: Sync](monitor: SMonitor): Monitor[F] = wrapUnsafe(monitor, Naming.defaultNaming())
  def wrapUnsafe[F[_]: Sync](monitor: SMonitor, naming: Naming): Monitor[F] = new impl.MonitorImpl(monitor, naming)

  def noOp[F[_]: Sync](): F[Monitor[F]] = {
    wrapJava(NoOpMonitor.INSTANCE)
  }

  def noOpUnsafe[F[_]: Sync](): Monitor[F] = {
    wrapJavaUnsafe(NoOpMonitor.INSTANCE)
  }

}
