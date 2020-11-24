package com.test.dixa.config

import cats.effect.{ Blocker, ContextShift, Sync }
import pureconfig.generic.semiauto.deriveReader
import pureconfig.module.catseffect.syntax._
import pureconfig.ConfigSource

import scala.concurrent.duration.FiniteDuration

case class StreamWindowParameters(amount: Int, time: FiniteDuration)

case class ServerConfig(port: Int)

case class Config(server: ServerConfig, streamWindowParameters: StreamWindowParameters)

object Config {

  implicit val streamWindowParametersReader = deriveReader[StreamWindowParameters]
  implicit val serverConfigReader           = deriveReader[ServerConfig]
  implicit val configReader                 = deriveReader[Config]

  def parseConfig[F[_]: Sync: ContextShift](blocker: Blocker) =
    ConfigSource.default.loadF[F, Config](blocker)
}
