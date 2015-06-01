package org.allenai.pipeline

import java.io.{ File, InputStream }

import org.allenai.common.Resource
import org.allenai.common.testkit.{ScratchDirectory, UnitSpec}
import org.allenai.common.testkit.{UnitSpec, ScratchDirectory}
import org.allenai.pipeline.ExternalProcess._
import org.allenai.pipeline.IoHelpers._
import org.allenai.pipeline.contrib.SamplingUtils
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfterAll}
import spray.json.DefaultJsonProtocol._

import scala.util.Random

/** Test Pipeline functionality */
class TestReservoirWithPersistPipeline extends UnitSpec
with BeforeAndAfterEach with BeforeAndAfterAll with ScratchDirectory {

  val inputDir = new File("src/test/resources/pipeline")
  val outputDataDir = new File(scratchDir, "data")

  def series_sh(l : Int) = {
    val script = new File(inputDir, "bash/series.sh")
    RunExternalProcess(
      "bash", script.getAbsolutePath(), l.toString()
    )(
        Seq(),
        versionHistory = Seq("v1.0")
      )
  }

  def cat_to_sh(in: Producer[Extarg]) = {
    val script = new File(inputDir, "bash/cat_to.sh")
    RunExternalProcess(
      "bash", script.getAbsolutePath(), "cat", OutputFileToken("output"),
      InputFileToken("input")
    )(
        Seq(in),
        versionHistory = Seq("v1.0")
      )
  }
  
  def lenShortInputStream(a : () => InputStream): Int = {
    Resource.using(a()) { b =>
      var ba = new Array[Byte](1000)
      val len = b.read(ba,0,1000)
      len
    }
  }
  
  def series_sh_10(l: Int): Producer[() => InputStream] = {
    val procSeries = series_sh(l)
    val outputSeries: Producer[() => InputStream] = procSeries.stdout //TODO: .outputs("output")
    outputSeries
  }

  "series.sh" should "produce at least one byte per row" in {
    val pipeline = Pipeline(scratchDir)

    val outputSeries: Producer[() => InputStream] = series_sh_10(10)

    pipeline.run("series_sh_one_byte_per_row")
    val len = lenShortInputStream(outputSeries.get)
    assert(len >= 10)
  }

  def series_to_sh_10_sampled: Producer[Extarg] = {
    val outputSeries: Producer[() => InputStream] = series_sh_10(10)

    // This is complicated
    val sampled =
      Producer.fromMemory(
        ExtargStream(
          JoinStream(
            SamplingUtils.RandomlySample2(
              ReadStreamContents(outputSeries),
              100,
              137
            )).get).asInstanceOf[Extarg])
    sampled
  }

  "RandomlySample2" should "produce five rows with at least one byte" in {
    val pipeline = Pipeline(scratchDir) // TODO: outputDataDir?

    val sampled: Producer[Extarg] = series_to_sh_10_sampled
  
  }

  "Test Reservoir with .persist" should "complete" in {
    val pipeline = Pipeline(scratchDir) // TODO: outputDataDir?

    val sampled: Producer[Extarg] = series_to_sh_10_sampled

    val procCapture = cat_to_sh(sampled)
    val outputCapture = procCapture.outputs("output")

    pipeline.persist(
      outputCapture,
      StreamIo,
      "sOut2",
      ".txt"
    )

    pipeline.run("Test Reservoir with .persist")
    print("")
  }


}
