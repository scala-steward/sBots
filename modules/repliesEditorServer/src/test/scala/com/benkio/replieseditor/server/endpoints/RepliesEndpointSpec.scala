package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.mocks.MockBotStore
import io.circe.Json
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.implicits.*

class RepliesEndpointSpec extends CatsEffectSuite {

  test("returns raw json") {
    val bs = new MockBotStore()
    val j  = Json.arr(Json.obj("x" -> Json.fromInt(1)))
    bs.getRepliesF = _ => IO.pure(Right(j))

    val app = new RepliesEndpoint(bs).routes.orNotFound
    val req = Request[IO](Method.GET, uri"/api/bot/cala/replies")
    for {
      resp <- app.run(req)
      body <- resp.as[Json]
    } yield {
      assertEquals(resp.status, Status.Ok)
      assertEquals(body, j)
    }
  }
}

