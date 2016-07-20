package com.avast.metrics.scala.oldstuff

import com.avast.metrics.api.Timer

import scala.concurrent.Future

class FutureTimer[A](val success: Timer, val failure: Timer, observer: FutureResultObserver[A]) {
  def future(future: => Future[A]): Future[A] = {
    val succCtx = success.start()
    val failCtx = failure.start()

    observer.observe(future, _ => succCtx.stop(), _ => failCtx.stop())
  }
}

class BlockTimer[A](val success: Timer, val failure: Timer, val observer: BlockResultObserver[A]) {
  def block(block: => A): A = {
    val succCtx = success.start()
    val failCtx = failure.start()

    observer.observe(block, _ => succCtx.stop(), _ => failCtx.stop())
  }
}
