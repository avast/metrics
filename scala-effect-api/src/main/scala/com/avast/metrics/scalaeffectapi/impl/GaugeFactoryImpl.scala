package com.avast.metrics.scalaeffectapi.impl

import cats.effect.Sync
import com.avast.metrics.scalaapi.{Monitor => SMonitor}
import com.avast.metrics.scalaeffectapi.{Gauge, GaugeFactory}

import java.util.concurrent.atomic.{AtomicLong, AtomicReference}

private class GaugeFactoryImpl[F[_]: Sync](monitor: SMonitor) extends GaugeFactory[F] {
  override def long(name: String): Gauge[F, Long] = new Gauge[F, Long] {
    private[this] val valueRef = new AtomicLong()
    private[this] val gauge = monitor.gauge(name)(valueRef.get)
    override def set(value: Long): F[Unit] = Sync[F].delay(valueRef.set(value))

    override def name: String = gauge.name

    override def value: F[Long] = Sync[F].delay(gauge.value)
  }

  override def double(name: String): Gauge[F, Double] = new Gauge[F, Double] {
    private[this] val valueRef = new AtomicReference(0.0)
    private[this] val gauge = monitor.gauge(name)(valueRef.get)
    override def set(value: Double): F[Unit] = Sync[F].delay(valueRef.set(value))

    override def name: String = gauge.name

    override def value: F[Double] = Sync[F].delay(gauge.value)
  }

}
