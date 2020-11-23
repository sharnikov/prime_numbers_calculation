package com.test.dixa

import java.util.concurrent.TimeUnit

import org.http4s.HttpService
import java.util.logging.Logger

import cats.effect.{ExitCode, IO, IOApp}
import org.lyranthe.fs2_grpc.java_runtime.implicits._
import dixa.primes.{CalculatorFs2Grpc, CalculatorGrpc}
import io.grpc.netty.NettyServerBuilder
import io.grpc.{Metadata, Server, ServerBuilder, ServerServiceDefinition}
import fs2.{Chunk, Stream => FStream}

import scala.concurrent.ExecutionContext
import scala.xml.MetaData

object CalcStarter extends IOApp {

  import cats.implicits._

  val calculatorService =
    CalculatorFs2Grpc.bindService(new CalculationService[IO, Metadata]())

  override def run(args: List[String]): IO[ExitCode] =
    NettyServerBuilder
      .forPort(9999)
      .keepAliveTime(500, TimeUnit.SECONDS)
      .addService(calculatorService)
      .stream[IO]
      .evalMap(server => IO(server.start()))
      .map(_.awaitTermination())
      .compile
      .drain
      .map(_ => ExitCode.Success)

}
