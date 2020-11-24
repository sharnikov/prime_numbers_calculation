package com.test.dixa.services

import cats.syntax.applicative._
import cats.syntax.functor._
import cats.effect.{ ConcurrentEffect, ContextShift, Sync, Timer }
import com.test.dixa.config.Config
import com.test.dixa.errors.Errors.{ CalculationFailedException, ExternalServerUnavailableException }
import dixa.primes.{ CalculatorFs2Grpc, Request }
import fs2.{ Chunk, Stream => FStream }
import io.chrisdavenport.log4cats.Logger
import io.grpc.{ ManagedChannelBuilder, Metadata, Status, StatusRuntimeException }

import scala.concurrent.duration._

object GrpcCalculatorService {
  def build[F[_]: ConcurrentEffect: ContextShift: Logger: Timer](config: Config): F[GrpcCalculatorService[F]] =
    Sync[F].delay(new GrpcCalculatorService[F](config))
}

trait CalculationService[F[_]] {
  def getPrimeStream(goalNumber: Int): FStream[F, Int]
  def getConvertedPrimeStream(goalNumber: Int): FStream[F, Byte]
}

class GrpcCalculatorService[F[_]: ConcurrentEffect: ContextShift: Logger: Timer] private (config: Config)
    extends CalculationService[F] {

  private val streamClient = {
    import org.lyranthe.fs2_grpc.java_runtime.implicits._

    val channel = ManagedChannelBuilder
      .forAddress(config.grpcEndpoint.host, config.grpcEndpoint.port)
      .usePlaintext()
      .stream[F]

    val request = Request(2)
    for {
      managedChannel <- channel
    } yield CalculatorFs2Grpc.stub(managedChannel)
  }

  override def getPrimeStream(goalNumber: Int): FStream[F, Int] =
    for {
      client <- streamClient
      request = Request(goalNumber)
      result <- tryToRequest(client, request, config.circuitBreaker.retryTimes, config.circuitBreaker.initialRetryDelay)
    } yield result

  private def tryToRequest(
      client: CalculatorFs2Grpc[F, Metadata],
      request: Request,
      timesToRetry: Int,
      timeToWait: FiniteDuration
  ): FStream[F, Int] =
    client.getPrimes(request, new Metadata()).attempt.flatMap {
      case Right(response) => FStream.emits(response.numbers)
      case Left(exception: StatusRuntimeException) if Status.UNAVAILABLE.getCode == exception.getStatus.getCode =>
        FStream
          .emit(())
          .evalTap { _ =>
            Logger[F].error(
              s"Calculation service it temporary unavailable. Exception message: ${exception.getMessage}."
            )
          }
          .flatMap(_ => FStream.raiseError(ExternalServerUnavailableException("Underling service is unavailable")))
      case Left(exception) if timesToRetry > 0 =>
        FStream
          .emit(())
          .evalTap { _ =>
            Logger[F].error(
              s"Prime calculation failed with ${exception.getMessage}. $timesToRetry retries left."
            )
          }
          .flatMap(_ => FStream.sleep[F](timeToWait))
          .flatMap(_ => tryToRequest(client, request, timesToRetry - 1, timeToWait * 2))
      case Left(exception) =>
        FStream
          .emit(())
          .evalTap { _ =>
            Logger[F].error(
              s"Prime calculation failed with ${exception.getMessage}."
            )
          }
          .flatMap(_ => FStream.raiseError(CalculationFailedException("Underling service has failed the request")))
    }

  override def getConvertedPrimeStream(goalNumber: Int): FStream[F, Byte] =
    getPrimeStream(goalNumber)
      .map(_.toString)
      .intersperse(", ")
      .flatMap(chars => FStream.chunk(Chunk.seq(chars)))
      .map(_.toByte)
}
