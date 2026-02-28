package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.mocks.MockBotStore
import io.circe.Json
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.implicits.*

class DeleteReplyEndpointSpec extends CatsEffectSuite {

  test("returns total on success") {
    val bs = new MockBotStore()
    bs.deleteAtF = (_, _) => IO.pure(Right(12))

    val app = new DeleteReplyEndpoint(bs).routes.orNotFound
    val req =
      Request[IO](Method.POST, uri"/api/bot/cala/replies/delete")
        .withEntity(Json.obj("index" -> Json.fromInt(0)))

    for {
      resp <- app.run(req)
      body <- resp.as[Json]
    } yield {
      assertEquals(resp.status, Status.Ok)
      assertEquals(body.hcursor.downField("ok").as[Boolean].toOption, Some(true))
      assertEquals(body.hcursor.downField("total").as[Int].toOption, Some(12))
    }
  }
}
