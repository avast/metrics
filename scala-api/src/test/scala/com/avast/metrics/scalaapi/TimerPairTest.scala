package com.avast.metrics.scalaapi

import com.avast.metrics.api.Timer.TimeContext
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class TimerPairTest extends FlatSpec with BeforeAndAfter with MockitoSugar {

  var success: Timer = _
  var succCtx: TimeContext = _
  var failure: Timer = _
  var failCtx: TimeContext = _
  var pair : TimerPair = _

  before {
    success = mock[Timer]
    succCtx = mock[TimeContext]
    when(success.start()).thenReturn(succCtx)

    failure = mock[Timer]
    failCtx = mock[TimeContext]
    when(failure.start()).thenReturn(failCtx)

    pair = new TimerPair(success, failure)
  }

  "time block" should "hit success timer if no exception is thrown" in {
    val expected: String = "noone expects spanish inquisition"

    assertResult(expected) { pair.time { expected } }

    verify(succCtx, times(1)).stop()
    verify(failCtx, times(0)).stop()
  }

  it should "hit failure timer if an exception is thrown and should rethrow the exception" in {
    val expected = new Exception

    intercept[Exception]{ pair.time { throw expected } }

    verify(succCtx, times(0)).stop()
    verify(failCtx, times(1)).stop()
  }

  "time future" should "hit success timer if the future is successful" in {
    val expected = "expected"
    val fut = Future.successful(expected)

    assertResult(expected) { Await.result(pair.time(fut), 10.seconds) }
    verify(succCtx, times(1)).stop()
    verify(failCtx, times(0)).stop()
  }

  it should "hit failure timer if the future is unsuccessful" in {
    val expected = new Exception
    val fut = Future.failed(expected)

    intercept[Exception]{ Await.result(pair.time(fut), 10.seconds) }
    verify(succCtx, times(0)).stop()
    verify(failCtx, times(1)).stop()
  }

  it should "hit failure timer if the block that generates future throws exception" in {
    val expected = new Exception

    intercept[Exception] { Await.result(pair.time { throw expected }, 10.seconds) }
    verify(succCtx, times(0)).stop()
    verify(failCtx, times(1)).stop()
  }
}
