package com.avast.metrics.scalaapi.impl

import com.avast.metrics.api.{Counter => JCounter}
import com.avast.metrics.scalaapi.Counter

private class CounterImpl(inner: JCounter) extends Counter {
  override def inc(): Unit = inner.inc()

  override def inc(n: Long): Unit = inner.inc(n)

  override def dec(): Unit = inner.dec()

  override def dec(n: Int): Unit = inner.dec(n)

  override def count: Long = inner.count()

  override def name: String = inner.getName
}
