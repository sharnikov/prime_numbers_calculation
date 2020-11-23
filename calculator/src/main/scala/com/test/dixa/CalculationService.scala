package com.test.dixa

import cats.Applicative
import dixa.primes.CalculatorGrpc.Calculator
import dixa.primes.{CalculatorFs2Grpc, Request, Response}
import fs2.{Chunk, Stream => FStream}
import cats.syntax.applicative._

class CalculationService[F[_]: Applicative, A] extends CalculatorFs2Grpc[F, A] {

  private val streamWithPrimes = LazyList.empty[Int]

  override def getPrimes(request: Request, ctx: A): F[Response] = {
    println("Got me")
    Response(Seq(1, 2, 3, 4, 5, 6, 6, 229)).pure[F]
  }

}
