package com.test.dixa

import cats.{FlatMap}
import cats.effect.Sync
import dixa.primes.{CalculatorFs2Grpc, Request, Response}
import cats.syntax.functor._
import com.test.dixa.calculation.PrimeCalculator

object CalculationService {
  def build[F[_]: Sync, A](
    primeCalculator: PrimeCalculator[F]
  ): F[CalculationService[F, A]] =
    Sync[F].delay(new CalculationService[F, A](primeCalculator))
}

class CalculationService[F[_]: FlatMap, A] private (
  primeCalculator: PrimeCalculator[F]
) extends CalculatorFs2Grpc[F, A] {

  override def getPrimes(request: Request, ctx: A): F[Response] =
    primeCalculator.getPrimes(request.number).map(Response(_))

}
