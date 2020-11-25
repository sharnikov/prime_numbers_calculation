package com.test.dixa.services

import fs2.{ Stream => FStream }
import cats.effect.IO
import com.test.dixa.config.{ CircuitBreaker, Config, GrpcEndpoint, ServerConfig }
import com.test.dixa.errors.Errors.{ CalculationFailedException, ExternalServerUnavailableException }
import dixa.primes.{ CalculatorFs2Grpc, Request, Response }
import io.grpc.{ Metadata, Status, StatusRuntimeException }
import utils.{ TestImplicits, TestUtils }

import scala.concurrent.duration._

class GrpcCalculatorServiceSpec extends TestUtils with TestImplicits {

  val config = Config(
    server = ServerConfig(
      host = "host",
      port = 1,
      requestTimeout = 10 seconds
    ),
    grpcEndpoint = GrpcEndpoint(
      host = "host",
      port = 123
    ),
    circuitBreaker = CircuitBreaker(
      retryTimes = 3,
      initialRetryDelay = 1 seconds
    )
  )

  behavior of "GrpcCalculatorService"

  trait mocks {
    val grpcService       = stub[CalculatorFs2Grpc[IO, Metadata]]
    val streamGrpcService = FStream.emit(grpcService).covary[IO]

    val grpcCalculator = GrpcCalculatorService.build[IO](config, streamGrpcService)
  }

  behavior of "GrpcCalculatorService"

  "getPrimeStream" should "calculate result correctly" in new mocks {
    val input = 10

    val grpcResultStream: FStream[IO, Response] =
      FStream.emits(List(Response(List(1, 2, 3, 4, 5)), Response(List(6, 7, 8, 8)))).covary[IO]

    (grpcService.getPrimes _).when(Request(input), *).returning(grpcResultStream)

    grpcCalculator.unsafeRunSync().getPrimeStream(input).compile.toList.unsafeRunSync() shouldBe List(
      1, 2, 3, 4, 5, 6, 7, 8, 8
    )

    (grpcService.getPrimes _).verify(*, *).once()
  }

  "getPrimeStream" should "faild with StatusRuntimeException instantly" in new mocks {
    val input = 10

    val exception = new StatusRuntimeException(Status.UNAVAILABLE)

    (grpcService.getPrimes _).when(Request(input), *).returning(FStream.eval(IO(throw exception)))

    awaitFailedStream[ExternalServerUnavailableException](
      grpcCalculator.unsafeRunSync().getPrimeStream(input),
      "Underling service is unavailable"
    )

    (grpcService.getPrimes _).verify(*, *).once()
  }

  "getPrimeStream" should "faild with Exception after several retries" in new mocks {
    val input = 10

    val exception = new Exception("Bad things happened")

    (grpcService.getPrimes _).when(Request(input), *).returning(FStream.eval(IO(throw exception)))

    awaitFailedStream[CalculationFailedException](
      grpcCalculator.unsafeRunSync().getPrimeStream(input),
      "Underling service has failed the request"
    )

    (grpcService.getPrimes _).verify(*, *).repeat(config.circuitBreaker.retryTimes)
  }

}
