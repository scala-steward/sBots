package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.mocks.MockBotStore
import com.benkio.replieseditor.server.module.ApiError
import io.circe.Json
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.implicits.*

class RepliesChunkEndpointSpec extends CatsEffectSuite {

  test("maps Unknown botId to 404") {
    val bs = new MockBotStore()
    bs.getRepliesChunkF = (_, _, _) => IO.pure(Left(ApiError("Unknown botId: x")))

    val app = new RepliesChunkEndpoint(bs).routes.orNotFound
    val req = Request[IO](Method.GET, uri"/api/bot/x/replies-chunk?offset=0&limit=10")
    for {
      resp <- app.run(req)
      body <- resp.as[Json]
    } yield {
      assertEquals(resp.status, Status.NotFound)
      assertEquals(body.hcursor.downField("error").as[String].toOption, Some("Unknown botId: x"))
    }
  }
}
