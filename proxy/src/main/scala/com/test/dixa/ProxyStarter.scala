package com.test.dixa

import cats.effect.{ ExitCode, IO, IOApp }
import com.test.dixa.config.Config
import com.test.dixa.modules.{ AppResources, Http, Services }
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.grpc.Metadata
import org.http4s.server.blaze.BlazeServerBuilder

object ProxyStarter extends IOApp {

  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    logger.info("Proxy service started to load") *>
      AppResources.build[IO]().use { resources =>
        for {
          config   <- Config.parseConfig[IO](resources.configBlocker)
          _        <- logger.info("Config is ready")
          services <- Services.build[IO](config)
          http     <- Http.build[IO](services, config)
          _        <- logger.info("Modules are built")
          _ <- BlazeServerBuilder[IO](resources.serverPool)
            .bindHttp(config.server.port, config.server.host)
            .withHttpApp(http.routes)
            .serve
            .compile
            .drain
        } yield ExitCode.Success
      }
}
