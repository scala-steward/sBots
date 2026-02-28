package com.benkio.replieseditor.server.store

import cats.effect.IO
import cats.effect.Resource
import com.benkio.telegrambotinfrastructure.model.reply.ReplyBundleMessage
import io.circe.syntax.*
import io.circe.Json
import munit.CatsEffectSuite

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class BotStoreMoreSpec extends CatsEffectSuite {

  private def write(path: Path, content: String): IO[Unit] =
    IO.blocking {
      Files.createDirectories(path.getParent)
      Files.writeString(path, content, StandardCharsets.UTF_8)
      ()
    }

  private def tempRepo: Resource[IO, Path] =
    Resource.make(IO.blocking(Files.createTempDirectory("sbots-replies-editor-test-more")))(p =>
      IO.blocking {
        def deleteRec(x: Path): Unit = {
          if Files.isDirectory(x) then {
            val stream = Files.list(x)
            try stream.forEach(deleteRec)
            finally stream.close()
          }
          Files.deleteIfExists(x)
          ()
        }
        deleteRec(p)
      }
    )

  test("BotStore.listBots filters botName not ending with Bot") {
    tempRepo.use { root =>
      val okDir    = root.resolve("modules").resolve("bots").resolve("OkBot")
      val nopeDir  = root.resolve("modules").resolve("bots").resolve("Nope")
      val okId     = "ok"
      val nopeId   = "nope"
      val okList   = okDir.resolve(s"${okId}_list.json")
      val okReply  = okDir.resolve("src").resolve("main").resolve("resources").resolve(s"${okId}_replies.json")
      val nopeList = nopeDir.resolve(s"${nopeId}_list.json")
      val nopeRep  = nopeDir.resolve("src").resolve("main").resolve("resources").resolve(s"${nopeId}_replies.json")

      val listContent =
        """[
          |  { "filename": "x.mp3", "sources": ["http://example.com/x.mp3"] }
          |]""".stripMargin
      val repliesContent = List(ReplyBundleMessage.textToText("hello")("a")).asJson.spaces2

      for {
        _     <- write(okList, listContent)
        _     <- write(okReply, repliesContent)
        _     <- write(nopeList, listContent)
        _     <- write(nopeRep, repliesContent)
        store <- BotStore.build(root)
        bots  <- store.listBots
      } yield {
        assertEquals(bots.map(_.botId), Vector(okId))
        assertEquals(bots.map(_.botName), Vector("OkBot"))
      }
    }
  }

  test("BotStore.reloadBotFromDisk refreshes cached replies") {
    tempRepo.use { root =>
      val botDir      = root.resolve("modules").resolve("bots").resolve("TestBot")
      val botId       = "test"
      val listJson    = botDir.resolve(s"${botId}_list.json")
      val repliesJson = botDir.resolve("src").resolve("main").resolve("resources").resolve(s"${botId}_replies.json")

      val listContent =
        """[
          |  { "filename": "a.mp3", "sources": ["http://example.com/a.mp3"] }
          |]""".stripMargin

      val r1 = List(ReplyBundleMessage.textToText("hello")("a"))
      val r2 = List(
        ReplyBundleMessage.textToText("hello")("a"),
        ReplyBundleMessage.textToText("bye")("b")
      )

      for {
        _     <- write(listJson, listContent)
        _     <- write(repliesJson, r1.asJson.spaces2)
        store <- BotStore.build(root)
        c1E   <- store.getRepliesChunk(botId, offset = 0, limit = 50)
        c1    <- IO.fromEither(c1E.left.map(e => new RuntimeException(e.error)))
        _     <- write(repliesJson, r2.asJson.spaces2) // update on disk
        _     <- store
          .reloadBotFromDisk(botId)
          .flatMap(e => IO.fromEither(e.left.map(err => new RuntimeException(err.error))))
        c2E <- store.getRepliesChunk(botId, offset = 0, limit = 50)
        c2  <- IO.fromEither(c2E.left.map(e => new RuntimeException(e.error)))
      } yield {
        assertEquals(c1.total, 1)
        assertEquals(c2.total, 2)
      }
    }
  }

  test("BotStore.updateReplyAt validates bounds") {
    tempRepo.use { root =>
      val botDir      = root.resolve("modules").resolve("bots").resolve("TestBot")
      val botId       = "test"
      val listJson    = botDir.resolve(s"${botId}_list.json")
      val repliesJson = botDir.resolve("src").resolve("main").resolve("resources").resolve(s"${botId}_replies.json")

      val listContent =
        """[
          |  { "filename": "a.mp3", "sources": ["http://example.com/a.mp3"] }
          |]""".stripMargin

      val replies = List(ReplyBundleMessage.textToText("hello")("a"))

      for {
        _     <- write(listJson, listContent)
        _     <- write(repliesJson, replies.asJson.spaces2)
        store <- BotStore.build(root)
        badE  <- store.updateReplyAt(botId, index = 99, value = Json.obj("x" -> Json.fromInt(1)))
        _ = assert(badE.isLeft)
        okE  <- store.updateReplyAt(botId, index = 0, value = Json.obj("updated" -> Json.fromBoolean(true)))
        _    <- IO.fromEither(okE.left.map(e => new RuntimeException(e.error)))
        repE <- store.getReplies(botId)
        rep  <- IO.fromEither(repE.left.map(e => new RuntimeException(e.error)))
      } yield {
        assertEquals(rep.asArray.map(_.length), Some(1))
        assertEquals(
          rep.asArray.flatMap(_.headOption).flatMap(_.hcursor.downField("updated").as[Boolean].toOption),
          Some(true)
        )
      }
    }
  }

  test("BotStore insertAt and deleteAt update totals") {
    tempRepo.use { root =>
      val botDir      = root.resolve("modules").resolve("bots").resolve("TestBot")
      val botId       = "test"
      val listJson    = botDir.resolve(s"${botId}_list.json")
      val repliesJson = botDir.resolve("src").resolve("main").resolve("resources").resolve(s"${botId}_replies.json")

      val listContent =
        """[
          |  { "filename": "a.mp3", "sources": ["http://example.com/a.mp3"] }
          |]""".stripMargin

      val replies = List(ReplyBundleMessage.textToText("hello")("a"))

      for {
        _     <- write(listJson, listContent)
        _     <- write(repliesJson, replies.asJson.spaces2)
        store <- BotStore.build(root)
        insE  <- store.insertAt(botId, index = 0, value = Json.obj("x" -> Json.fromInt(1)))
        n1    <- IO.fromEither(insE.left.map(e => new RuntimeException(e.error)))
        delE  <- store.deleteAt(botId, index = 0)
        n2    <- IO.fromEither(delE.left.map(e => new RuntimeException(e.error)))
      } yield {
        assertEquals(n1, 2)
        assertEquals(n2, 1)
      }
    }
  }

  test("BotStore reports load errors for invalid json files") {
    tempRepo.use { root =>
      val botDir      = root.resolve("modules").resolve("bots").resolve("TestBot")
      val botId       = "test"
      val listJson    = botDir.resolve(s"${botId}_list.json")
      val repliesJson = botDir.resolve("src").resolve("main").resolve("resources").resolve(s"${botId}_replies.json")

      for {
        _        <- write(listJson, "{ not json")
        _        <- write(repliesJson, "{ not json")
        store    <- BotStore.build(root)
        allowedE <- store.getAllowedFiles(botId)
        repliesE <- store.getReplies(botId)
      } yield {
        assert(allowedE.left.exists(_.error.contains("Failed to load allowed files")))
        assert(repliesE.left.exists(_.error.contains("Failed to load replies")))
      }
    }
  }
}
