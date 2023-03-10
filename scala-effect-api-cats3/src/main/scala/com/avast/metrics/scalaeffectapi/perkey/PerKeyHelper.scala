package com.avast.metrics.scalaeffectapi.perkey

import cats.data.NonEmptyList
import com.avast.metrics.scalaeffectapi.Monitor

private[perkey] object PerKeyHelper {
  type MetricBuilder[T] = NonEmptyList[String] => T

  def metricBuilder[F[_], T](
      monitor: Monitor[F]
  )(baseName: String, instanceBuilder: (Monitor[F], String) => T): MetricBuilder[T] = { keys =>
    if (keys.length == 1) {
      instanceBuilder(monitor.named(baseName), keys.last)
    } else {
      val tailWithoutLast = keys.tail.dropRight(1)
      instanceBuilder(monitor.named(baseName, keys.head, tailWithoutLast: _*), keys.last)
    }
  }
}
