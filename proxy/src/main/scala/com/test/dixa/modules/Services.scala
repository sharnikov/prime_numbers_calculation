package com.test.dixa.modules

import cats.effect.{ ConcurrentEffect, ContextShift }
import cats.syntax.functor._
import com.test.dixa.config.Config
import com.test.dixa.services.{ CalculationService, GrpcCalculatorService }
import io.chrisdavenport.log4cats.Logger

object Services {

  def build[F[_]: ConcurrentEffect: ContextShift: Logger](config: Config): F[Services[F]] =
    for {
      rpcCalculationService <- GrpcCalculatorService.build[F](config)
    } yield new Services[F](rpcCalculationService)

}

final class Services[F[_]] private (val rpcCalculationService: CalculationService[F])
