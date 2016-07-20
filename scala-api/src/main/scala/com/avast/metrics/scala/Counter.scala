package com.avast.metrics.scala

import com.avast.metrics.api.{Counter => JCounter}
import com.avast.metrics.scala.api.{Counter => ICounter}

class Counter(inner: JCounter) extends ICounter {
  override def inc(): Unit = inner.inc()

  override def inc(n: Long): Unit = inner.inc(n)

  override def dec(): Unit = inner.dec()

  override def dec(n: Int): Unit = inner.dec(n)

  override def count: Long = inner.count()

  override def name: String = inner.getName
}
