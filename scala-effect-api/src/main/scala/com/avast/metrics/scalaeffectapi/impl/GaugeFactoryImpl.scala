package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import cats.effect.std.Dispatcher
import com.avast.metrics.scalaapi.{Monitor => SMonitor}
import com.avast.metrics.scalaeffectapi.{Gauge, GaugeFactory, SettableGauge}

import java.util.concurrent.atomic.{AtomicLong, AtomicReference}

private class GaugeFactoryImpl[F[_]: Sync](monitor: SMonitor) extends GaugeFactory[F] {

  override def settableLong(name: String, replaceExisting: Boolean = false): SettableGauge[F, Long] = new SettableGauge[F, Long] {
    private[this] val valueRef = new AtomicLong(0)
    private[this] val gauge = monitor.gauge(name, replaceExisting)(valueRef.get)

    override def set(value: Long): F[Unit] = Sync[F].delay(valueRef.set(value))

    override def update(f: Long => Long): F[Long] = Sync[F].delay(valueRef.updateAndGet(f(_)))

    override def inc: F[Long] = Sync[F].delay(valueRef.incrementAndGet())

    override def dec: F[Long] = Sync[F].delay(valueRef.decrementAndGet())

    override def name: String = gauge.name

    override def value: F[Long] = Sync[F].delay(gauge.value)
  }

  override def settableDouble(name: String, replaceExisting: Boolean = false): SettableGauge[F, Double] = new SettableGauge[F, Double] {
    private[this] val valueRef = new AtomicReference(0.0)
    private[this] val gauge = monitor.gauge(name, replaceExisting)(valueRef.get)

    override def set(value: Double): F[Unit] = Sync[F].delay(valueRef.set(value))

    override def update(f: Double => Double): F[Double] = Sync[F].delay(valueRef.updateAndGet(f(_)))

    override def inc: F[Double] = Sync[F].delay(valueRef.accumulateAndGet(1, (a, b) => a + b))

    override def dec: F[Double] = Sync[F].delay(valueRef.accumulateAndGet(1, (a, b) => a - b))

    override def name: String = gauge.name

    override def value: F[Double] = Sync[F].delay(gauge.value)
  }

  override def generic[T](name: String, replaceExisting: Boolean = false)(retrieveValue: () => T): Gauge[F, T] = new Gauge[F, T] {
    private[this] val gauge = monitor.gauge(name, replaceExisting)(retrieveValue)

    override def value: F[T] = Sync[F].delay(gauge.value)

    override def name: String = gauge.name
  }

  override def genericWithUnsafeRun[T](name: String, replaceExisting: Boolean)(retrieveValue: F[T])(implicit
      dispatcher: Dispatcher[F]
  ): Gauge[F, T] = generic(name, replaceExisting)(() => dispatcher.unsafeRunSync(retrieveValue))
}
