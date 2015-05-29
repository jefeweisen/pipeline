package org.allenai.pipeline.examples

import java.lang.Iterable

import org.allenai.pipeline.IoHelpers._
import org.allenai.pipeline._
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.{ObjectId, Ref, Repository}
import org.eclipse.jgit.revwalk.{RevTree, RevCommit, RevWalk}
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk

import spray.json.DefaultJsonProtocol._


import java.io.File

object HelloJgit extends App {
  def walkTree(tree: RevTree) = {
    val treeWalk = new TreeWalk(repository);
    treeWalk.addTree(tree);
    treeWalk.setRecursive(false);
    while (treeWalk.next()) {
      if (treeWalk.isSubtree()) {
        System.out.println("dir: " + treeWalk.getPathString());
        treeWalk.enterSubtree();
      } else {
        System.out.println("file: " + treeWalk.getPathString());
      }
    }
  }

  def fileoptAtPath(tree: RevTree, a:String) = {
    val treeWalk = new TreeWalk(repository);
    treeWalk.addTree(tree);
    treeWalk.setRecursive(false);
    var fileopt : Option[ObjectId] = None
    var cont = true
    while (treeWalk.next()) {
      if (treeWalk.isSubtree()) {
        treeWalk.enterSubtree();
      } else {
        val b = treeWalk.getPathString();
        if(a==b) {
          fileopt = Some(treeWalk.getObjectId(0))
          cont = false
        }
      }
    }
    fileopt
  }

  def stringoptFromOid(repository:Repository, oid:ObjectId) = {
    val beginning = repository.getObjectDatabase().open(oid).getBytes().take(100)
    Some(new String(beginning))
  }
  
  def stringoptFromFileAt(repository:Repository, b:Iterable[RevCommit], path:String) = {
    val en = log.iterator();
    var cMax = 5
    var ret : Option[String] = None
    while(en.hasNext() && cMax > 0) {
      val commit = en.next();
      val id = commit.toObjectId();
      println(s"found ${id.getClass} ${id.toString()}");
      val tree: RevTree = commit.getTree()
      fileoptAtPath(tree, path) match {
        case Some(oid) =>
          ret = stringoptFromOid(repository,oid)
      }
      cMax = cMax - 1;
    }
    ret
  }

  val dirCw : String = System.getProperty("user.dir");
  val fileRepo = new File(new File(dirCw),  "../../../3p/atom").getCanonicalFile()
  val filename1 = "src/config.coffee"

  println(s"repo= ${fileRepo.getAbsolutePath}")
  if(!fileRepo.exists())
    throw new RuntimeException("git directory not found")
  val builder : FileRepositoryBuilder = new FileRepositoryBuilder();
  val repository : Repository = builder.setGitDir(new File(fileRepo, ".git"))
    .readEnvironment() // scan environment GIT_* variables
    .findGitDir() // scan up the file system tree
    .build();

  val head0 : Ref = repository.getRef("refs/heads/master");
  println(s"found class=${head0.getClass()}")
  val oid = head0.getObjectId()
  println(s"found objectId=${oid}")

  val git : Git = new Git(repository);
  
  val log : Iterable[RevCommit] = git.log().call();
  val st = stringoptFromFileAt(repository, log, filename1)
  println(s"    $st")

  /*
  Iterator(log).foreach{
    case commit:RevCommit =>
  }
  */
}
