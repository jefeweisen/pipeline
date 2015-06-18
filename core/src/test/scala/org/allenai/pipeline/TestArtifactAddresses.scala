package org.allenai.pipeline

import java.io.File
import java.net.URI

import org.allenai.common.testkit.{ScratchDirectory, UnitSpec}

class TestArtifactAddresses extends UnitSpec with ScratchDirectory {


  "aipout artifacts" should "write" in {
    val dirAipout = new File(scratchDir, "aipout")
    val pipeline = new Pipeline {
      override def rootOutputUrl: URI = dirAipout.toURI()
      override def urlToArtifact = UrlToArtifact.chain(CreateCoreArtifacts.fromFileUrls, CreateCoreArtifacts.fromFileUrls)
    }
    val stepName = "stuff"
    val suffix = ".txt"
    val original : Producer[Unit] = null

    // extract: artifactOut?
    val path : String = s"data/$stepName.${original.stepInfo.signature.id}$suffix"
    val artifact = pipeline.createOutputArtifact[FlatArtifact](path)
  }
  //val dirAipexo = new File(scratchDir, "aipexo")
  /*      def credentials: S3Credentials = S3Config.environmentCredentials()
        CreateCoreArtifacts.fromS3Urls(credentials)*/

  def lookup1(pipeline:Pipeline, stURI: String): File = {

  }
  def lookup2(pipeline:Pipeline, stURI: String, stID: String, stExt: String): File = {

  }
  "aipexo artifacts" should "resolve" in {
    val dirAipout = new File(scratchDir, "aipout")
    val pipeline = new Pipeline {
      override def rootOutputUrl: URI = dirAipout.toURI()

      override def urlToArtifact =
        UrlToArtifact.chain(CreateCoreArtifacts.fromFileUrls, CreateCoreArtifacts.fromFileUrls)
    }
    lookup1(pipeline, "aipexo://special/2015-01-01/snowflake.txt") should equal(
      new File(dirAipexo, "special/2015-01-01/snowflake.txt")
    )
  }

  "aipout artifacts" should "resolve" in {
    val dirAipout = new File(scratchDir, "aipout")
    val dirAipexo = new File(scratchDir, "aipexo")
    val pipeline = new Pipeline {
      override def rootOutputUrl: URI = dirAipout.toURI()

      override def urlToArtifact =
        UrlToArtifact.chain(CreateCoreArtifacts.fromFileUrls, CreateCoreArtifacts.fromFileUrls)
    }
    val signatureId = "012345"
    lookup2(pipeline, "aipout://special/2015-01-01/snowflake", signatureId, ".txt") should equal(
      new File(dirAipout, s"special/2015-01-01/snowflake_${signatureId}.txt")
    )
  }

  "failover" should "failover" in {
    val dirAipout = new File(scratchDir, "aipout")
    val dirMockS3 = new File(scratchDir, "mocks3")
    val pipeline = new Pipeline {
      override def rootOutputUrl: URI = dirAipout.toURI()

      override def urlToArtifact = CreateCoreArtifacts.fromFileUrls(dirAipout.toURI())

      def urlToArtifactForRead =
        UrlToArtifact.chain(
          CreateCoreArtifacts.fromFileUrls(dirAipout.toURI()),
          CreateCoreArtifacts.fromFileUrls(dirMockS3.toURI()))

      override def artifactFactory = ArtifactFactory(urlToArtifact, urlToArtifactForRead)
    }

  }

}



object TestArtifactAddresses {
  def persistNoop[T,A](){() =>
  }

}