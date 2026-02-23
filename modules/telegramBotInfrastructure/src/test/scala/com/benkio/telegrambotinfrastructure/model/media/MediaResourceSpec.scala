package com.benkio.telegrambotinfrastructure.model.media

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.*
import com.benkio.telegrambotinfrastructure.model.media.MediaResource.MediaResourceFile
import com.benkio.telegrambotinfrastructure.model.media.MediaResource.MediaResourceIFile
import munit.*
import telegramium.bots.InputLinkFile
import telegramium.bots.InputPartFile

import java.io.File
import java.nio.file.Path

class MediaResourceSpec extends CatsEffectSuite {
  test("toTelegramApi should return the expected Telegram Type") {
    val file      = File(".")
    val actual1   = MediaResourceFile(Resource.pure[IO, Path](file.toPath())).toTelegramApi
    val expected1 = InputPartFile(file)
    // --------------------------------------------------
    val ifile                            = "ifile"
    val mediaResource: MediaResource[IO] = MediaResourceIFile(ifile)
    val actual2                          = mediaResource.toTelegramApi
    val expected2                        = InputLinkFile(ifile)
    actual1.use(assertEquals(_, expected1).pure[IO]) *>
      actual2.use(assertEquals(_, expected2).pure[IO])
  }
  test("getMediaResourceFile should extrac the file or return None") {
    val file      = File(".")
    val actual1   = MediaResourceFile(Resource.pure[IO, Path](file.toPath())).getMediaResourceFile.sequence
    val expected1 = Some(file.toPath())

    val ifile                            = "ifile"
    val mediaResource: MediaResource[IO] = MediaResourceIFile(ifile)
    val actual2                          = mediaResource.getMediaResourceFile
    assertIO(actual1.use(_.pure[IO]), expected1) *>
      assertIO(actual2.pure[IO], None)
  }
}
