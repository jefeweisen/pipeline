package org.allenai.pipeline.examples

import org.allenai.pipeline.IoHelpers._
import org.allenai.pipeline._

import spray.json.DefaultJsonProtocol._

import org.eclipse.jgit.api._

import java.io.File

object HelloJgit extends App {
  FileRepositoryBuilder builder = new FileRepositoryBuilder();
  Repository repository = builder.setGitDir(new File("/my/git/directory"))
    .readEnvironment() // scan environment GIT_* variables
    .findGitDir() // scan up the file system tree
    .build();
}
