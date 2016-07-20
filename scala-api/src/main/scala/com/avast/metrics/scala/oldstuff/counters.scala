package com.avast.metrics.scala.oldstuff

import com.avast.metrics.api.Counter

import scala.concurrent.Future

class FutureCounters[A](success: Counter, failure: Counter, futureResultObserver: FutureResultObserver[A]) {
  def count(fut: => Future[A]): Future[A] = {
    futureResultObserver.observe(fut, _ => success.inc(), _ => failure.inc())
  }
}

class BlockCounters[A](success: Counter, failure: Counter, blockResultObserver: BlockResultObserver[A]) {
  def count(block: => A): A = {
    blockResultObserver.observe(block, _ => success.inc(), _ => failure.inc())
  }
}