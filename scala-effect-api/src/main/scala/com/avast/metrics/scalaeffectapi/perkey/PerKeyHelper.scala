package com.avast.metrics.scalaeffectapi.perkey

import cats.data.NonEmptyList
import com.avast.metrics.scalaeffectapi.Monitor

object PerKeyHelper {

  def metricBuilder[F[_], T](
      monitor: Monitor[F]
  )(baseName: String, instanceBuilder: (Monitor[F], String) => T): NonEmptyList[String] => T = { keys =>
    if (keys.length == 1) {
      instanceBuilder(monitor.named(baseName), keys.last)
    } else {
      val tailWithoutLats = keys.tail.drop(keys.length - 1)
      instanceBuilder(monitor.named(baseName, keys.head, tailWithoutLats: _*), keys.last)
    }
  }
}
