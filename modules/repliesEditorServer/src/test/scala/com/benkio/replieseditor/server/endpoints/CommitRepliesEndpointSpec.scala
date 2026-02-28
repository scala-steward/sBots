package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.mocks.MockBotStore
import com.benkio.replieseditor.server.module.ApiError
import io.circe.Json
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.implicits.*

class CommitRepliesEndpointSpec extends CatsEffectSuite {

  test("maps validation error to 400") {
    val bs = new MockBotStore()
    bs.commitF = _ => IO.pure(Left(ApiError("Some media files are not present in *_list.json")))

    val app = new CommitRepliesEndpoint(bs).routes.orNotFound
    val req = Request[IO](Method.POST, uri"/api/bot/cala/replies/commit")
    for {
      resp <- app.run(req)
      body <- resp.as[Json]
    } yield {
      assertEquals(resp.status, Status.BadRequest)
      assertEquals(
        body.hcursor.downField("error").as[String].toOption,
        Some("Some media files are not present in *_list.json")
      )
    }
  }
}
