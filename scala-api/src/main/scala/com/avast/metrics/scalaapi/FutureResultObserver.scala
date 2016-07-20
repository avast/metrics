package com.avast.metrics.scalaapi

import scala.concurrent.Future
import scala.util.{Failure, Success}

class FutureResultObserver[A] {
  def observe(future: => Future[A], onSuccess: A => Unit, onFailure: Throwable => Unit): Future[A] = {
    try {
      val fut = future

      future.onComplete {
        case Success(a) =>
          onSuccess(a)
        case Failure(thr) =>
          onFailure(thr)
      }

      fut
    } catch {
      case thr: Throwable =>
        onFailure(thr)
        throw thr
    }
  }
}
