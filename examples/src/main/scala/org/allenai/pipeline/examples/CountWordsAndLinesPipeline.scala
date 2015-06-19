package org.allenai.pipeline.examples

import org.allenai.pipeline.IoHelpers._
import org.allenai.pipeline._

import spray.json.DefaultJsonProtocol._

import java.io.File

/** A simple pipeline that counts words and lines in a text file */
object CountWordsAndLinesPipeline extends App {
  // Create a pipeline.  Specify the output directory where data will be written
  val pipeline = Pipeline(new File("pipeline-output"))

  val exo = new File("/Users/fred/box%20sync")
  // Define our input:  A collection of lines read from an inputFile
  val textFile = new File(exo, "core/src/test/resources/pipeline/features.txt")
  // Must import IoHelpers._ to enable this
  val lines = Read.Collection.fromText[String](textFile)

  pipeline.perist(lines, StreamIo, "hint", ".txt")

  val textFile = new File("hint.txt")
  pipeline.persistedSteps("hint") // returns lines

  // ways to remove home dir from signature
  // hacky way #1:
  lines.copy(stepInfo = lines.stepInfo.addParameters("src" -> "rel path"))

  // hacky way #2:
  class BoxArtifact extends FlatArtifact //(or extend FileArtifact ?)

  /*
  class S3FlatArtifact(
                        val path: String,
                        val config: S3Config,
                        val contentTypeOverride: Option[String] = None
                        )
    extends FlatArtifact with S3Artifact[FileArtifact] {
  */
  //has the mount prefix. removes the prefix when reading/writing
  //then this will work:

  Read.Collection.fromText[String](textFile)

  // make a url box://

  val wordCount = {
    // The Producer instance
    val count = CountWords(lines)
    // Persist this step
    // Must import spray.json.DefaultJsonProtocol._ to enable this
    pipeline.Persist.Singleton.asJson(count)
  }

  val lineCount = {
    // The Producer instance
    val count = CountLines(lines)
    // Persisted
    pipeline.Persist.Singleton.asText(count)
  }

  // Run the pipeline
  val steps = pipeline.run("Count words and lines")
  if(steps.isEmpty) throw new RuntimeException("Unsuccessful pipeline") // for unit test
}

