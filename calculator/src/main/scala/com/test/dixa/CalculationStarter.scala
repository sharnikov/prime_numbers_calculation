package com.test.dixa

import cats.effect.{ ExitCode, IO, IOApp }
import com.test.dixa.modules.Services
import org.lyranthe.fs2_grpc.java_runtime.implicits._
import dixa.primes.CalculatorFs2Grpc
import io.grpc.netty.NettyServerBuilder
import io.grpc.Metadata
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object CalculationStarter extends IOApp {

  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    logger.info("Calculation service started to load") *>
      (for {
        services <- Services.build[IO, Metadata]()
        bindCalculationService = CalculatorFs2Grpc.bindService(
          services.calculationService
        )
        _ <- NettyServerBuilder
          .forPort(9999)
          .addService(bindCalculationService)
          .stream[IO]
          .evalMap(server => IO(server.start()))
          .map(_.awaitTermination())
          .compile
          .drain
      } yield ExitCode.Success)

}
