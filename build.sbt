lazy val root = (project in file("."))
  .settings(
    name := "dixa-test-task",
    scalaVersion := "2.13.4"
  )
  .dependsOn(calculator, proxy)
  .aggregate(calculator, proxy)

lazy val proxy = (project in file("proxy"))
  .settings(
    name := "proxy",
    scalaVersion := "2.13.4",
    libraryDependencies ++= Dependencies.proxy,
    commonCompileSettings
  )

lazy val calculator = (project in file("calculator"))
  .settings(
    name := "calculator",
    scalaVersion := "2.13.4",
    libraryDependencies ++= Dependencies.calculator,
    commonCompileSettings
  )

lazy val commonCompileSettings = Seq(
  scalacOptions := Seq(
    "-language:higherKinds",
    "-Ypartial-unification"
  ),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)