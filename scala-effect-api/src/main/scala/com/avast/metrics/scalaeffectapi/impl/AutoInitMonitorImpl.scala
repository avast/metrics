package com.avast.metrics.scalaeffectapi.impl

import cats.effect.kernel.Sync
import cats.effect.std.Dispatcher
import cats.syntax.apply._
import com.avast.metrics.api.Naming
import com.avast.metrics.scalaapi.{Monitor => SMonitor}
import com.avast.metrics.scalaeffectapi.{Counter, Histogram, Meter, Timer, TimerPair}

import scala.concurrent.duration.Duration

/** WARNING: This class create side effecting implementation of [[MonitorImpl]]
  * The side effect is that each metric is directly sent with its default value (typically zero) on its creation.
  * This is a kind of hack in order to let StatsD know about all kinds of metrics that we use. Otherwise it's
  * quite difficult to work with sparse metrics as they do not exist at the beginning, which in turn makes dashboard
  * definition quite hard. Typical example would be some kind of rare error that is difficult to trigger.
  */
class AutoInitMonitorImpl[F[_]: Sync](monitor: SMonitor, naming: Naming)(implicit dispatcher: Dispatcher[F])
    extends MonitorImpl[F](monitor, naming) {

  override def meter(name: String): Meter[F] = {
    val result = super.meter(name)
    unsafeEval(result.mark(0))
    result
  }

  override def counter(name: String): Counter[F] = {
    val result = super.counter(name)
    unsafeEval(result.inc(0))
    result
  }

  override def timer(name: String): Timer[F] = {
    val result = super.timer(name)
    unsafeEval(result.update(Duration.Zero))
    result
  }

  override def timerPair(name: String): TimerPair[F] = {
    val result = super.timerPair(name)
    unsafeEval(result.update(Duration.Zero) *> result.updateFailure(Duration.Zero))
    result
  }

  override def histogram(name: String): Histogram[F] = {
    val result = super.histogram(name)
    unsafeEval(result.update(0L))
    result
  }

  private def unsafeEval[T](f: F[T]): Unit = dispatcher.unsafeRunAndForget(f)
}
