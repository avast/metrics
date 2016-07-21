package com.avast.metrics.scalaapi

import com.avast.metrics.api.{Counter => JCounter}

class Counter(inner: JCounter) extends api.Counter {
  override def inc(): Unit = inner.inc()

  override def inc(n: Long): Unit = inner.inc(n)

  override def dec(): Unit = inner.dec()

  override def dec(n: Int): Unit = inner.dec(n)

  override def count: Long = inner.count()

  override def name: String = inner.getName
}
