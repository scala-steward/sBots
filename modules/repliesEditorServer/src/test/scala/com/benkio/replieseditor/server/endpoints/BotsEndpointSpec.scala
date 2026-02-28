package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.mocks.MockBotStore
import com.benkio.replieseditor.server.module.ApiBot
import io.circe.Json
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.implicits.*

class BotsEndpointSpec extends CatsEffectSuite {

  test("returns bots") {
    val bs = new MockBotStore()
    bs.listBotsF = () => IO.pure(Vector(ApiBot("cala", "CalandroBot")))

    val app = new BotsEndpoint(bs).routes.orNotFound
    val req = Request[IO](Method.GET, uri"/api/bots")
    for {
      resp <- app.run(req)
      body <- resp.as[Json]
    } yield {
      assertEquals(resp.status, Status.Ok)
      assertEquals(body, Json.arr(Json.obj("botId" -> Json.fromString("cala"), "botName" -> Json.fromString("CalandroBot"))))
    }
  }
}

