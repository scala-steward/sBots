package com.benkio.replieseditor.server.load

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object RepoRoot {

  private def sysPropRepoRoot: Option[Path] =
    Option(System.getProperty("sbots.repoRoot"))
      .map(Paths.get(_).toAbsolutePath.normalize())
      .filter(p => Files.isDirectory(p.resolve("modules").resolve("bots")))

  def detect(start: Path = Paths.get("").toAbsolutePath.normalize()): Path =
    sysPropRepoRoot.getOrElse(detectByWalkingParents(start))

  private def detectByWalkingParents(start: Path): Path = {
    def isRepoRoot(p: Path): Boolean =
      Files.isRegularFile(p.resolve("build.sbt")) &&
        Files.isDirectory(p.resolve("modules").resolve("bots"))

    Iterator
      .iterate(Option(start))(_.flatMap(p => Option(p.getParent)))
      .takeWhile(_.isDefined)
      .flatten
      .find(isRepoRoot)
      .getOrElse(start)
  }
}
