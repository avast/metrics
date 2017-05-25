package com.avast.metrics.scalaapi.perkey.impl

import com.avast.metrics.scalaapi.perkey.PerKeyMetric

import scala.collection.concurrent.{Map => CMap}

private[perkey] class PerKeyMetricImpl[A](map: CMap[String, A], metricBuilder: (String) => A) extends PerKeyMetric[A] {
  override def forKey(str: String): A = {
    map.getOrElseUpdate(str, metricBuilder(str))
  }
}
