package com.test.dixa

import java.util.concurrent.TimeUnit

import cats.effect.{ Concurrent, Sync, Timer }
import dixa.primes.{ CalculatorFs2Grpc, Request, Response }
import com.test.dixa.calculation.PrimeCalculator
import fs2.{ Stream => FStream }

import scala.concurrent.duration.FiniteDuration

object CalculationService {
  def build[F[_]: Concurrent: Timer, A](
      primeCalculator: PrimeCalculator[F]
  ): F[CalculationService[F, A]] =
    Sync[F].delay(new CalculationService[F, A](primeCalculator))
}

class CalculationService[F[_]: Concurrent: Timer, A] private (
    primeCalculator: PrimeCalculator[F]
) extends CalculatorFs2Grpc[F, A] {

  override def getPrimes(request: Request, ctx: A): FStream[F, Response] =
    primeCalculator
      .getPrimes(request.number)
      .groupWithin(100, FiniteDuration(500, TimeUnit.MILLISECONDS))
      .map(chunk => Response(chunk.toList))
}
