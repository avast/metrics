package com.avast.metrics.scalaeffectapi.perkey.impl

import cats.data.NonEmptyList
import com.avast.metrics.scalaeffectapi.perkey.PerKeyHelper.MetricBuilder
import com.avast.metrics.scalaeffectapi.perkey.PerKeyMetric

import scala.collection.concurrent.{Map => CMap}

private[perkey] class PerKeyMetricImpl[A](map: CMap[String, A], metricBuilder: MetricBuilder[A]) extends PerKeyMetric[A] {
  override def forKey(name: String): A = {
    map.getOrElseUpdate(name, metricBuilder(NonEmptyList(name, Nil)))
  }

  override def forKeys(name: String, name2: String, names: String*): A = {
    val keys = List(name, name2) ::: names.toList
    map.getOrElseUpdate(keys.mkString("."), metricBuilder(NonEmptyList(name, keys.tail)))
  }
}
