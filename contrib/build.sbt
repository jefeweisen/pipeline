import Dependencies._

name := "pipeline-contrib"
organization := "org.allenai"

StylePlugin.enableLineLimit := false

libraryDependencies ++= Seq(
  ai2Common,
  allenAiTestkit % "test",
  "com.sangupta" % "murmur" % "1.0.0"
)
