package com.test.dixa.modules

import cats.effect.{ContextShift, Sync}
import cats.syntax.functor._
import com.test.dixa.services.{CalculationService, RpcCalculationService}

import io.chrisdavenport.log4cats.Logger

object Services {

  def build[F[_]: Sync: ContextShift: Logger](): F[Services[F]] =
    for {
      rpcCalculationService <- RpcCalculationService.build[F]
    } yield new Services[F](rpcCalculationService)

}

final class Services[F[_]] private (val rpcCalculationService: CalculationService[F])
