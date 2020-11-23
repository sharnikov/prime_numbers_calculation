package com.test.dixa

import cats.effect.{ExitCode, IO, IOApp}
import com.test.dixa.modules.{AppResources, Http, Services}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

object ProxyStarter extends IOApp {

  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    logger.info("Proxy service started to load") *>
      AppResources.build[IO]().use { resources =>
        (for {
          services <- Services.build[IO]()
          http <- Http.build[IO](services)
          _ <- BlazeServerBuilder[IO](resources.serverPool)
            .bindHttp(8083, "localhost")
            .withHttpApp(http.routes)
            .serve
            .compile
            .drain
        } yield ExitCode.Success)
      }

}
