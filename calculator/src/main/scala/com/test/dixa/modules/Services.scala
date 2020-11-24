package com.test.dixa.modules

import cats.syntax.functor._
import cats.syntax.flatMap._
import cats.effect.{ Concurrent, Timer }
import com.test.dixa.CalculationService
import com.test.dixa.calculation.{ BrutePrimeCalculator, PrimeCalculator }

object Services {
  def build[F[_]: Concurrent: Timer, A](): F[Services[F, A]] =
    for {
      brutePrimeCalculator <- BrutePrimeCalculator.build[F]
      calculationService   <- CalculationService.build[F, A](brutePrimeCalculator)
    } yield new Services[F, A](brutePrimeCalculator, calculationService)
}

final class Services[F[_], A] private (
    val brutePrimeCalculator: PrimeCalculator[F],
    val calculationService: CalculationService[F, A]
)
