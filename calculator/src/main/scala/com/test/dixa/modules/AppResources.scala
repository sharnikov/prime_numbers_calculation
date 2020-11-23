package com.test.dixa.modules

import java.util.concurrent.{ ExecutorService, Executors }

import cats.syntax.functor._
import cats.syntax.applicative._
import cats.syntax.apply._
import cats.effect.{ Blocker, Resource, Sync }

import scala.concurrent.ExecutionContext

final case class Resources[F[_]](configBlocker: Blocker)

object AppResources {

  def build[F[_]: Sync](): Resource[F, Resources[F]] = {

    def configBlocker(): Resource[F, Blocker] = {
      val allocation   = Sync[F].delay(Executors.newCachedThreadPool)
      val freeFunction = (es: ExecutorService) => Sync[F].delay(es.shutdown())
      Resource.make(allocation)(freeFunction).map(ExecutionContext.fromExecutor).map(Blocker.liftExecutionContext)
    }

    configBlocker().map(Resources.apply)
  }
}
