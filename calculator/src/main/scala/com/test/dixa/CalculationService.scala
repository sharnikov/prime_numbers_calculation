package com.test.dixa

import dixa.primes.CalculatorGrpc.Calculator
import dixa.primes.{Request, Response}

import scala.concurrent.Future


class CalculationService extends Calculator {
  override def getPrimes(request: Request): Future[Response] = {
    println(s"Got ${request.number}")
    Future.successful(Response(Seq(1,2,3,4,5,6,6,7,8,8,9)))
  }
}
