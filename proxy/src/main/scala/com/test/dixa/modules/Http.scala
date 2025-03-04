package com.test.dixa.modules

import cats.effect.{ Concurrent, ContextShift, Sync, Timer }
import cats.syntax.semigroupk._
import com.test.dixa.config.Config
import com.test.dixa.services.ApiRoutes
import org.http4s.server.middleware.{ AutoSlash, CORS, RequestLogger, ResponseLogger, Timeout }
import org.http4s.{ HttpApp, HttpRoutes }
import org.http4s.syntax.kleisli._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import io.chrisdavenport.log4cats.Logger

object Http {
  def build[F[_]: ContextShift: Concurrent: Logger: Timer](services: Services[F], config: Config): F[Http[F]] =
    Sync[F].delay(new Http[F](services, config))
}

final class Http[F[_]: ContextShift: Concurrent: Logger: Timer] private (services: Services[F], config: Config) {

  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[F] =>
      CORS(http, CORS.DefaultCORSConfig)
    } andThen { http: HttpRoutes[F] =>
      Timeout(config.server.requestTimeout)(http)
    }
  }

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] =>
      RequestLogger.httpApp(logHeaders = true, logBody = true)(http)
    } andThen { http: HttpApp[F] =>
      ResponseLogger.httpApp(logHeaders = true, logBody = true)(http)
    }
  }

  private val api = new ApiRoutes[F](services.rpcCalculationService)

  private val docs       = api.endpoints.toOpenAPI("Dixa test task API", "v1")
  private val docsRoutes = new SwaggerHttp4s(docs.toYaml)

  private val routesWithSwagger = api.routes <+> docsRoutes.routes

  val routes: HttpApp[F] = loggers(middleware(routesWithSwagger).orNotFound)

}
