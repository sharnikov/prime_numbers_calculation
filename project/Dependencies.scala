import sbt.ModuleID
import sbt.librarymanagement.{ DependencyBuilders, LibraryManagementSyntax }

object Dependencies extends DependencyBuilders with LibraryManagementSyntax {

  object Versions {
    val catsStm    = "0.8.0"
    val cats       = "2.2.0"
    val tapir      = "0.17.0-M1"
    val http4s     = "0.21.9"
    val logback    = "1.2.3"
    val log4cats   = "1.1.1"
    val fs2        = "2.4.4"
    val scalaTest  = "3.2.3"
    val pureConfig = "0.14.0"
  }

  private def log4CatsLib(artifact: String): ModuleID   = "io.chrisdavenport"           %% artifact % Versions.log4cats
  private def logbackLib(artifact: String): ModuleID    = "ch.qos.logback"               % artifact % Versions.logback
  private def http4sLib(artifact: String): ModuleID     = "org.http4s"                  %% artifact % Versions.http4s
  private def tapirLib(artifact: String): ModuleID      = "com.softwaremill.sttp.tapir" %% artifact % Versions.tapir
  private def fs2Lib(artifact: String): ModuleID        = "co.fs2"                      %% artifact % Versions.fs2
  private def pureConfigLib(artifact: String): ModuleID = "com.github.pureconfig"       %% artifact % Versions.pureConfig
  private def scalaTestLib(artifact: String): ModuleID  = "org.scalatest"               %% artifact % Versions.scalaTest % Test

  private val cats = Seq(
    "org.typelevel"        %% "cats-core"   % Versions.cats,
    "org.typelevel"        %% "cats-effect" % Versions.cats,
    "io.github.timwspence" %% "cats-stm"    % Versions.catsStm
  )

  private val fs2 = Seq(
    fs2Lib("fs2-core"),
    fs2Lib("fs2-io")
  )

  private val tapir = Seq(
    tapirLib("tapir-core"),
    tapirLib("tapir-swagger-ui-http4s"),
    tapirLib("tapir-json-circe"),
    tapirLib("tapir-openapi-docs"),
    tapirLib("tapir-http4s-server"),
    tapirLib("tapir-openapi-circe-yaml")
  )

  private val http4s = Seq(
    http4sLib("http4s-dsl"),
    http4sLib("http4s-blaze-server"),
    http4sLib("http4s-blaze-client"),
    "com.softwaremill.sttp" %% "async-http-client-backend-fs2" % "1.6.7"
  )

  private val log = Seq(
    logbackLib("logback-classic"),
    logbackLib("logback-core"),
    log4CatsLib("log4cats-slf4j"),
    log4CatsLib("log4cats-core"),
    "org.slf4j"            % "slf4j-api"                % "1.7.19",
    "net.logstash.logback" % "logstash-logback-encoder" % "4.11"
  )

  private val pureConfig = Seq(
    pureConfigLib("pureconfig"),
    pureConfigLib("pureconfig-cats"),
    pureConfigLib("pureconfig-cats-effect")
  )

  private val grpc = Seq(
    "io.grpc" % "grpc-netty"    % "1.33.1",
    "io.grpc" % "grpc-services" % "1.33.1"
  )

  private val test = Seq(
    scalaTestLib("scalatest"),
    scalaTestLib("scalatest-shouldmatchers"),
    "org.scalacheck" %% "scalacheck" % "1.14.3" % Test,
    "org.scalamock"  %% "scalamock"  % "4.4.0"  % Test
  )

  val proxy = cats ++ tapir ++ http4s ++ grpc ++ log ++ test ++ fs2 ++ pureConfig

  val calculator = cats ++ grpc ++ log ++ test ++ fs2 ++ pureConfig
}
