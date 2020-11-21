package com.test.dixa

import cats.syntax.flatMap._
import cats.effect.{ExitCode, IO, IOApp}
import com.test.dixa.modules.{Http, Services}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

object ProxyStarter extends IOApp {

  implicit val logger = Slf4jLogger.getLogger[IO]

  val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  override def run(args: List[String]): IO[ExitCode] =
    Logger[IO].info("Calculator service started to load") *>
      (for {
    services <- Services.build[IO]()
    http <- Http.build[IO](services)
    _ <- BlazeServerBuilder[IO](ec)
      .bindHttp(8080, "localhost")
      .withHttpApp(http.routes)
      .serve
      .compile
      .drain
  } yield ExitCode.Success)

}
