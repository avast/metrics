package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import cats.effect.std.Dispatcher
import com.avast.metrics.scalaapi.{Monitor => SMonitor}
import com.avast.metrics.scalaeffectapi.{Gauge, GaugeFactory, SettableGauge}

import java.util.concurrent.atomic.{AtomicLong, AtomicReference}

private class GaugeFactoryImpl[F[_]: Sync](monitor: SMonitor) extends GaugeFactory[F] {

  override def long(name: String, replaceExisting: Boolean = false): SettableGauge[F, Long] = new SettableGauge[F, Long] {
    private[this] val valueRef = new AtomicLong(0)
    private[this] val gauge = monitor.gauge(name, replaceExisting)(valueRef.get)

    override def set(value: Long): F[Unit] = Sync[F].delay(valueRef.set(value))

    override def name: String = gauge.name

    override def value: F[Long] = Sync[F].delay(gauge.value)
  }

  override def double(name: String, replaceExisting: Boolean = false): SettableGauge[F, Double] = new SettableGauge[F, Double] {
    private[this] val valueRef = new AtomicReference(0.0)
    private[this] val gauge = monitor.gauge(name, replaceExisting)(valueRef.get)

    override def set(value: Double): F[Unit] = Sync[F].delay(valueRef.set(value))

    override def name: String = gauge.name

    override def value: F[Double] = Sync[F].delay(gauge.value)
  }

  override def forType[T](name: String, replaceExisting: Boolean = false)(retrieveValue: () => T): Gauge[F, T] = new Gauge[F, T] {
    private[this] val gauge = monitor.gauge(name, replaceExisting)(retrieveValue)

    override def value: F[T] = Sync[F].delay(gauge.value)

    override def name: String = gauge.name
  }

  override def forTypeWithUnsafeRun[T](name: String, replaceExisting: Boolean)(retrieveValue: F[T])(implicit
      dispatcher: Dispatcher[F]
  ): Gauge[F, T] = forType(name, replaceExisting)(() => dispatcher.unsafeRunSync(retrieveValue))
}
