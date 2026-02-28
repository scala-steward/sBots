package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.mocks.MockBotStore
import com.benkio.replieseditor.server.module.SaveOk
import io.circe.Json
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.implicits.*

class SaveRepliesEndpointSpec extends CatsEffectSuite {

  test("decodes [] and returns ok") {
    val bs = new MockBotStore()
    bs.saveRepliesF = (botId, replies) => {
      assertEquals(botId, "cala")
      assertEquals(replies, Nil)
      IO.pure(Right(SaveOk(botId = "cala", repliesCount = 0)))
    }

    val app = new SaveRepliesEndpoint(bs).routes.orNotFound
    val req = Request[IO](Method.POST, uri"/api/bot/cala/replies").withEntity(Json.arr())
    for {
      resp <- app.run(req)
      body <- resp.as[Json]
    } yield {
      assertEquals(resp.status, Status.Ok)
      assertEquals(body.hcursor.downField("botId").as[String].toOption, Some("cala"))
      assertEquals(body.hcursor.downField("repliesCount").as[Int].toOption, Some(0))
    }
  }
}

