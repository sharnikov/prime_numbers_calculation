package com.test.dixa.config

import cats.effect.{ Blocker, ContextShift, Sync }
import pureconfig.generic.semiauto.deriveReader
import pureconfig.module.catseffect.syntax._
import pureconfig.ConfigSource

case class GrpcEndpoint(host: String, port: Int)

case class ServerConfig(host: String, port: Int)

case class Config(server: ServerConfig, grpcEndpoint: GrpcEndpoint)

object Config {

  implicit val grpcEndpointReader = deriveReader[GrpcEndpoint]
  implicit val serverConfigReader = deriveReader[ServerConfig]
  implicit val configReader       = deriveReader[Config]

  def parseConfig[F[_]: Sync: ContextShift](blocker: Blocker) =
    ConfigSource.default.loadF[F, Config](blocker)
}
