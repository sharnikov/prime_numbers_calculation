package com.test.dixa

import cats.effect.{ Concurrent, Sync, Timer }
import dixa.primes.{ CalculatorFs2Grpc, Request, Response }
import com.test.dixa.calculation.PrimeCalculator
import com.test.dixa.config.Config
import fs2.{ Stream => FStream }

object CalculationService {
  def build[F[_]: Concurrent: Timer, A](
      primeCalculator: PrimeCalculator[F],
      config: Config
  ): F[CalculationService[F, A]] =
    Sync[F].delay(new CalculationService[F, A](primeCalculator, config))
}

class CalculationService[F[_]: Concurrent: Timer, A] private (
    primeCalculator: PrimeCalculator[F],
    config: Config
) extends CalculatorFs2Grpc[F, A] {

  override def getPrimes(request: Request, ctx: A): FStream[F, Response] =
    primeCalculator
      .getPrimes(request.number)
      .groupWithin(config.streamWindowParameters.amount, config.streamWindowParameters.time)
      .map(chunk => Response(chunk.toList))
}
