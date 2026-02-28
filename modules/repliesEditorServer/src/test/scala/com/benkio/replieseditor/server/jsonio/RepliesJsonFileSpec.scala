package com.benkio.replieseditor.server.jsonio

import cats.effect.IO
import munit.CatsEffectSuite

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

class RepliesJsonFileSpec extends CatsEffectSuite {

  private def tempDir(): Path =
    Files.createTempDirectory("replies-json-file-test-").toAbsolutePath.normalize()

  test("writePretty creates parents and writes") {
    val dir  = tempDir()
    val path = dir.resolve("a").resolve("b").resolve("replies.json")
    for {
      _ <- RepliesJsonFile.writePretty(path, """{ "x": 1 }""")
      s <- IO.blocking(Files.readString(path, StandardCharsets.UTF_8))
    } yield assertEquals(s, """{ "x": 1 }""")
  }

  test("read parses json") {
    val dir  = tempDir()
    val path = dir.resolve("replies.json")
    Files.writeString(path, """{ "x": 1 }""", StandardCharsets.UTF_8)
    RepliesJsonFile.read(path).map { j =>
      assertEquals(j.hcursor.downField("x").as[Int].toOption, Some(1))
    }
  }
}

