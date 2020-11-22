package com.test.dixa.services

import cats.effect.{ConcurrentEffect, ContextShift, IO, Sync}
import dixa.primes.{CalculatorFs2Grpc, CalculatorGrpc, Request}
import fs2.{Chunk, Stream => FStream}
import io.chrisdavenport.log4cats.Logger
import io.grpc.ManagedChannelBuilder

import scala.concurrent.Await
import scala.concurrent.duration.Duration
//import io.grpc.{ManagedChannel, ManagedChannelBuilder}

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

//    val channel = ManagedChannelBuilder.forAddress("localhost", 9999).stream[F]
//    val request = Request(10)
//
//    for {
//      managedChannel <- channel
//      client = CalculatorGrpc.stub(managedChannel)
//      result = client.getPrimes(request)
//      streamResult <- FStream.eval(result.)
//    } yield streamResult
    val channel = ManagedChannelBuilder.forAddress("localhost", 9999).usePlaintext().build
    val request = Request(10)

    val res = CalculatorGrpc.stub(channel).getPrimes(request)

    val pureRes = Await.result(res, Duration.Inf)

    FStream.emits(pureRes.numbers)
  }
//    FStream.range(0, 5)



  override def getConvertedPrimeStream(goalNumber: Int): FStream[F, Byte] =
    getPrimeStream(goalNumber)
      .map(_.toString)
      .intersperse(", ")
      .flatMap(chars => FStream.chunk(Chunk.seq(chars)))
      .map(_.toByte)
}

