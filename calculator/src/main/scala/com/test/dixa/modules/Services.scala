package com.test.dixa.modules

import cats.syntax.functor._
import cats.syntax.flatMap._
import cats.effect.Sync
import com.test.dixa.CalculationService
import com.test.dixa.calculation.{BrutePrimeCalculator, PrimeCalculator}
import io.chrisdavenport.log4cats.Logger

object Services {
  def build[F[_]: Sync: Logger, A](): F[Services[F, A]] =
    for {
      brutePrimeCalculator <- BrutePrimeCalculator.build[F]
      calculationService <- CalculationService.build[F, A](brutePrimeCalculator)
    } yield new Services[F, A](brutePrimeCalculator, calculationService)
}

final class Services[F[_], A] private (
  val brutePrimeCalculator: PrimeCalculator[F],
  val calculationService: CalculationService[F, A]
)
