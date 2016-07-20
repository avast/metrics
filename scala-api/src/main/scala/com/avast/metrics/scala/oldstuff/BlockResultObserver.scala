package com.avast.metrics.scala.oldstuff

import scala.util.control.NonFatal

class BlockResultObserver[A] {
  def observe(block: => A, onSuccess: A => Unit, onFailure: Throwable => Unit): A = {
    try {
      val res = block
      onSuccess(res)
      res
    } catch {
      case NonFatal(ex) =>
        onFailure(ex)
        throw ex
    }
  }
}
