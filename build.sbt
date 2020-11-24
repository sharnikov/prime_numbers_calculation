lazy val root = (project in file("."))
  .settings(
    name := "dixa-test-task",
    scalaVersion := "2.13.4"
  )
  .enablePlugins(Fs2Grpc)
  .dependsOn(calculator, proxy)
  .aggregate(calculator, proxy)

lazy val proxy = (project in file("proxy"))
  .settings(
    name := "proxy",
    scalaVersion := "2.13.4",
    libraryDependencies ++= Dependencies.proxy,
    commonCompileSettings
  )
  .enablePlugins(Fs2Grpc)
  .dependsOn(common)

lazy val calculator = (project in file("calculator"))
  .settings(
    name := "calculator",
    scalaVersion := "2.13.4",
    libraryDependencies ++= Dependencies.calculator,
    commonCompileSettings
  )
  .enablePlugins(Fs2Grpc)
  .dependsOn(common)

lazy val common = (project in file("common"))
  .settings(
    name := "common",
    scalaVersion := "2.13.4",
    commonCompileSettings
  )
  .enablePlugins(Fs2Grpc)

lazy val commonCompileSettings = Seq(
  scalacOptions := Seq(
    "-language:higherKinds"
  ),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)

PB.targets in compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value / "scalapb"
)
