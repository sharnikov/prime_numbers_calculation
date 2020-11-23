package com.test.dixa.services

import cats.syntax.functor._
import cats.effect.{ConcurrentEffect, ContextShift, Sync}
import dixa.primes.{CalculatorFs2Grpc, Request}
import fs2.{Chunk, Stream => FStream}
import io.chrisdavenport.log4cats.Logger
import io.grpc.{ManagedChannelBuilder, Metadata}

object GrpcCalculatorService {
  def build[F[_]: ConcurrentEffect: ContextShift: Logger]: F[GrpcCalculatorService[F]] =
    Sync[F].delay(new GrpcCalculatorService[F])
}

trait CalculationService[F[_]] {
  def getPrimeStream(goalNumber: Int): FStream[F, Int]
  def getConvertedPrimeStream(goalNumber: Int): FStream[F, Byte]
}

class GrpcCalculatorService[F[_]: ConcurrentEffect: ContextShift: Logger] private() extends CalculationService[F] {
  override def getPrimeStream(goalNumber: Int): FStream[F, Int] = {

    import org.lyranthe.fs2_grpc.java_runtime.implicits._

    val channel = ManagedChannelBuilder
      .forAddress("localhost", 9999)
      .usePlaintext()
      .stream[F]
    val request = Request(10)

    val metaData = new Metadata

    for {
      managedChannel <- channel
      client = CalculatorFs2Grpc.stub(managedChannel)
      result = client.getPrimes(request, metaData).map(_.numbers)
      streamResult <- FStream.evalSeq(result).covary[F]
    } yield streamResult

  }

  override def getConvertedPrimeStream(goalNumber: Int): FStream[F, Byte] =
    getPrimeStream(goalNumber)
      .map(_.toString)
      .intersperse(", ")
      .flatMap(chars => FStream.chunk(Chunk.seq(chars)))
      .map(_.toByte)
}

