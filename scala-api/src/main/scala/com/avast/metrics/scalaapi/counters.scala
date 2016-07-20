package com.avast.metrics.scalaapi

import com.avast.metrics.api.Counter

import scala.concurrent.Future

class FutureCounters[A](success: Counter, failure: Counter, futureResultObserver: FutureResultObserver[A]) {
  def count(fut: => Future[A]) = {
    futureResultObserver.observe(fut, _ => success.inc(), _ => failure.inc())
  }
}

class BlockCounters[A](success: Counter, failure: Counter, blockResultObserver: BlockResultObserver[A]) {
  def count(block: => A) = {
    blockResultObserver.observe(block, _ => success.inc(), _ => failure.inc())
  }
}