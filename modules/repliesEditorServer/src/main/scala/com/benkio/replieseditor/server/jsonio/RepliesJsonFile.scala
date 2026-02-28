package com.benkio.replieseditor.server.jsonio

import cats.effect.IO
import io.circe.parser.parse
import io.circe.Json

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

object RepliesJsonFile {
  def read(path: Path): IO[Json] =
    IO.blocking(Files.readString(path, StandardCharsets.UTF_8)).flatMap { raw =>
      parse(raw) match {
        case Left(pf)    => IO.raiseError(new RuntimeException(pf.message))
        case Right(json) => IO.pure(json)
      }
    }

  def writePretty(path: Path, content: String): IO[Unit] =
    IO.blocking {
      Files.createDirectories(path.getParent)
      Files.writeString(path, content, StandardCharsets.UTF_8)
      ()
    }
}
