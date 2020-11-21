package com.test.dixa.services

import java.nio.charset.StandardCharsets

import cats.effect.{ContextShift, Sync}
import cats.syntax.applicative._
import cats.syntax.either._
import cats.syntax.functor._
import cats.syntax.semigroupk._
import org.http4s.HttpRoutes
import io.chrisdavenport.log4cats.Logger
import sttp.model.StatusCode
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._


class ApiRoutes[F[_]: Sync: ContextShift: Logger](calculationService: CalculationService[F]) {

  private val getPrimeStreamEndpoint =
    endpoint.get
      .in("prime" / path[Int]("number")
        .description("Number to limit prime response prime number stream")
      )//.errorOut(defaultErrors)
      .out(streamBody(Fs2Streams[F], schemaFor[String], CodecFormat.TextPlain(), Some(StandardCharsets.UTF_8)))
      .out(statusCode(StatusCode.Ok))

  private val healthCheckEndpoint =
    endpoint.get
      .in("health")
      .out(jsonBody[String])
      .out(statusCode(StatusCode.Ok))

  private val getPrimeStreamRoute = getPrimeStreamEndpoint.toRoutes(
    calculationService.getConvertedPrimeStream(_).pure[F].map(Right(_))
  )

  private val healthCheckRoute = healthCheckEndpoint.toRoutes(_ => "It's fine".asRight[Unit].pure[F])

  val endpoints = List(getPrimeStreamEndpoint, healthCheckEndpoint).map(_.tag("All routes"))

  val routes: HttpRoutes[F] = getPrimeStreamRoute <+> healthCheckRoute

}
