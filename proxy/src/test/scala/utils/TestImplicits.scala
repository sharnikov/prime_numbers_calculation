package utils

import cats.effect.{ ContextShift, IO, Timer }
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

import scala.concurrent.ExecutionContext.Implicits.global

trait TestImplicits {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO]               = IO.timer(global)
  implicit val logger                         = Slf4jLogger.getLogger[IO]
}
