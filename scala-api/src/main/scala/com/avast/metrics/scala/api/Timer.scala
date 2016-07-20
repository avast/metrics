package com.avast.metrics.scala.api

import com.avast.metrics.api.Timer.TimeContext

import scala.concurrent.duration.Duration

trait Timer extends Metric{

  def start(): TimeContext

  def update(duration: Duration): Unit

  def update(duration: java.time.Duration): Unit
}
