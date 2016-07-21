package com.avast.metrics.scalaapi.api

import java.time.Duration

import com.avast.metrics.api.Timer.TimeContext

trait Timer extends Metric {

  def start(): TimeContext
  def update(duration: Duration): Unit
}
