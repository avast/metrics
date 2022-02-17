package com.avast.metrics.scalaeffectapi.perkey.impl

import com.avast.metrics.scalaeffectapi.perkey.PerKeyMetric

import scala.collection.concurrent.{Map => CMap}

private[perkey] class PerKeyMetricImpl[A](map: CMap[String, A], metricBuilder: String => A) extends PerKeyMetric[A] {
  override def forKey(str: String): A = {
    map.getOrElseUpdate(str, metricBuilder(str))
  }
}
