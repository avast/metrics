package com.avast.metrics.scala.api

import com.avast.metrics.api.Timer.TimeContext

import scala.concurrent.Future
import scala.concurrent.duration.Duration

trait Timer {

  def start(): TimeContext

  def update(duration: Duration): Unit

  def update(duration: java.time.Duration): Unit

  def time[A](fn: () => A): A

  def time[A](block: => A): A

  def time[A](future: => Future[A]): Future[A]

  def time[A](fn: () => A, failure: Timer): A

  def time[A](block: => A, failure: Timer): A

  def time[A](future: => Future[A], failure: Timer): Future[A]

}
