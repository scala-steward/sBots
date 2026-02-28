package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.mocks.MockBotStore
import io.circe.Json
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.implicits.*

class AllowedFilesEndpointSpec extends CatsEffectSuite {

  test("returns list") {
    val bs = new MockBotStore()
    bs.getAllowedFilesF = _ => IO.pure(Right(Vector("a.jpg", "b.mp4")))

    val app = new AllowedFilesEndpoint(bs).routes.orNotFound
    val req = Request[IO](Method.GET, uri"/api/bot/cala/allowed-files")
    for {
      resp <- app.run(req)
      body <- resp.as[Json]
    } yield {
      assertEquals(resp.status, Status.Ok)
      assertEquals(body, Json.arr(Json.fromString("a.jpg"), Json.fromString("b.mp4")))
    }
  }
}
