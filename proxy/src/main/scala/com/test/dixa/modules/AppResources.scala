package com.test.dixa.modules

import java.util.concurrent.{Executors, TimeUnit}

import cats.syntax.functor._
import cats.syntax.applicative._
import cats.Applicative
import cats.effect.Resource

import scala.concurrent.ExecutionContext

final case class Resources[F[_]](serverPool: ExecutionContext)

object AppResources {

  def build[F[_]: Applicative](): Resource[F, Resources[F]] = {

    def serverPool(): Resource[F, ExecutionContext] =
      Resource
        .make(Executors.newCachedThreadPool().pure[F]) {
          _.awaitTermination(5, TimeUnit.SECONDS).pure[F].map(_ => ())
        }
        .map(ExecutionContext.fromExecutor)

    serverPool().map(pool => Resources[F](pool))
  }
}
