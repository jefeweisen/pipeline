import Dependencies._

name := "pipeline-examples"
organization := "org.allenai"

test in assembly := {}

StylePlugin.enableLineLimit := false

libraryDependencies ++= Seq(
  allenAiTestkit % "test",
  awsJavaSdk
)
