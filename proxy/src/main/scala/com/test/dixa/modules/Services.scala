package com.test.dixa.modules

import cats.effect.{ ConcurrentEffect, ContextShift, Sync, Timer }
import cats.syntax.functor._
import cats.syntax.flatMap._
import com.test.dixa.config.Config
import com.test.dixa.services.{ CalculationService, GrpcCalculatorService }
import dixa.primes.CalculatorFs2Grpc
import io.chrisdavenport.log4cats.Logger
import io.grpc.{ ManagedChannelBuilder, Metadata }
import fs2.{ Stream => FStream }

object Services {

  def build[F[_]: ConcurrentEffect: ContextShift: Logger: Timer](config: Config): F[Services[F]] =
    for {
      buildGrpcClient       <- buildGrpcClient[F](config)
      rpcCalculationService <- GrpcCalculatorService.build[F, Metadata](config, buildGrpcClient)
    } yield new Services[F](rpcCalculationService, buildGrpcClient)

  private def buildGrpcClient[F[_]: ConcurrentEffect](
      config: Config
  ): F[FStream[F, CalculatorFs2Grpc[F, Metadata]]] =
    Sync[F].delay {
      import org.lyranthe.fs2_grpc.java_runtime.implicits._

      val channel = ManagedChannelBuilder
        .forAddress(config.grpcEndpoint.host, config.grpcEndpoint.port)
        .usePlaintext()
        .stream[F]

      for {
        managedChannel <- channel
      } yield CalculatorFs2Grpc.stub(managedChannel)
    }

}

final class Services[F[_]] private (
    val rpcCalculationService: CalculationService[F],
    val buildGrpcClient: FStream[F, CalculatorFs2Grpc[F, Metadata]]
)
