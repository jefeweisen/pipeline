package org.allenai.pipeline.examples

import org.allenai.pipeline.IoHelpers._
import org.allenai.pipeline._

import spray.json.DefaultJsonProtocol._

import java.io.File

/** A simple pipeline that counts words and lines in a text file */
object CountWordsAndLinesPipeline extends App {
  // Create a pipeline.  Specify the output directory where data will be written
  val pipeline = Pipeline.saveToFileSystem(new File("pipeline-output"))

  // Define our input:  A collection of lines read from an inputFile
  val textFile = new File("src/test/resources/pipeline/features.txt")
  // Must import IoHelpers._ to enable this
  val lines = Read.Collection.fromText[String](textFile)

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
  pipeline.run("Count words and lines")
}

