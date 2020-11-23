package com.test.dixa.config

import cats.effect.{ Blocker, ContextShift, Sync }
import pureconfig.generic.semiauto.deriveReader
import pureconfig.module.catseffect.syntax._
import pureconfig.ConfigSource

case class ServerConfig(port: Int)

case class Config(server: ServerConfig)

object Config {

  implicit val serverConfigReader = deriveReader[ServerConfig]
  implicit val configReader       = deriveReader[Config]

  def parseConfig[F[_]: Sync: ContextShift](blocker: Blocker) =
    ConfigSource.default.loadF[F, Config](blocker)
}
