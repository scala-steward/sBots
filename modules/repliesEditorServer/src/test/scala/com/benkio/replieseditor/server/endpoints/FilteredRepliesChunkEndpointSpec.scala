package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.mocks.MockBotStore
import com.benkio.replieseditor.server.module.{IndexedReply, RepliesChunk}
import io.circe.Json
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.implicits.*

class FilteredRepliesChunkEndpointSpec extends CatsEffectSuite {

  test("posts FilterReq and returns chunk") {
    val bs = new MockBotStore()
    val chunk =
      RepliesChunk(
        total = 1,
        offset = 0,
        items = Vector(IndexedReply(10, Json.obj("a" -> Json.fromInt(1))))
      )

    bs.getFilteredRepliesChunkF = (botId, msg, off, lim) => {
      assertEquals(botId, "cala")
      assertEquals(msg, "hello")
      assertEquals(off, 0)
      assertEquals(lim, 50)
      IO.pure(Right(chunk))
    }

    val app = new FilteredRepliesChunkEndpoint(bs).routes.orNotFound
    val req =
      Request[IO](Method.POST, uri"/api/bot/cala/replies-filter-chunk")
        .withEntity(Json.obj("message" -> Json.fromString("hello")))

    for {
      resp <- app.run(req)
      body <- resp.as[Json]
    } yield {
      assertEquals(resp.status, Status.Ok)
      assertEquals(body.hcursor.downField("total").as[Int].toOption, Some(1))
      assertEquals(body.hcursor.downField("items").downArray.downField("index").as[Int].toOption, Some(10))
    }
  }
}

