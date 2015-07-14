import Dependencies._

name := "pipeline-s3"
organization := "org.allenai"

test in assembly := {}

StylePlugin.enableLineLimit := false

libraryDependencies ++= Seq(
  awsJavaSdk
)
