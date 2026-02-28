package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.mocks.MockBotStore
import io.circe.Json
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.implicits.*

class UpdateReplyEndpointSpec extends CatsEffectSuite {

  test("returns ok true on success") {
    val bs = new MockBotStore()
    bs.updateReplyAtF = (_, _, _) => IO.pure(Right(()))

    val app = new UpdateReplyEndpoint(bs).routes.orNotFound
    val req =
      Request[IO](Method.POST, uri"/api/bot/cala/replies/update")
        .withEntity(Json.obj("index" -> Json.fromInt(1), "value" -> Json.obj("x" -> Json.fromInt(1))))

    for {
      resp <- app.run(req)
      body <- resp.as[Json]
    } yield {
      assertEquals(resp.status, Status.Ok)
      assertEquals(body, Json.obj("ok" -> Json.fromBoolean(true)))
    }
  }
}

