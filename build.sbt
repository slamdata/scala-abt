import sbt._, Keys._

import slamdata.CommonDependencies
import slamdata.SbtSlamData.transferPublishAndTagResources

lazy val baseSettings = commonBuildSettings ++ Seq(
  organization := "com.slamdata",
  libraryDependencies += CommonDependencies.slamdata.predef,
  wartremoverWarnings in (Compile, compile) --= Seq(
    Wart.ImplicitParameter
  )
)

lazy val publishSettings = commonPublishSettings ++ Seq(
  organizationName := "SlamData Inc.",
  organizationHomepage := Some(url("http://slamdata.com")),
  homepage := Some(url("https://github.com/slamdata/scala-abt")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/slamdata/scala-abt"),
      "scm:git@github.com:slamdata/scala-abt.git"
    )
  ))

lazy val allSettings =
  baseSettings ++ publishSettings

lazy val root = (project in file("."))
  .aggregate(core)
  .settings(allSettings)
  .settings(noPublishSettings)
  .settings(transferPublishAndTagResources)
  .settings(Seq(
    name := "abt"
  ))

lazy val core = (project in file("core"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(allSettings)
  .settings(Seq(
    name := "abt-core",
    libraryDependencies ++= Seq(
      CommonDependencies.scalaz.core
    )
  ))
