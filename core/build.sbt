import Dependencies._

name := "pipeline-core"
organization := "org.allenai"

test in assembly := {}

StylePlugin.enableLineLimit := false

dependencyOverrides += "org.scala-lang" % "scala-reflect" % "2.11.5"
libraryDependencies ++= Seq(
  sprayJson,
  commonsIO,
  apacheCommonsCodec,
  ai2Common,
  allenAiTestkit % "test",
  scalaReflection
)
