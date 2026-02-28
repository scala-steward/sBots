package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.mocks.MockBotStore
import com.benkio.replieseditor.server.module.ApiError
import io.circe.Json
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.implicits.*

class ReloadBotEndpointSpec extends CatsEffectSuite {

  test("returns ok true on success") {
    val bs = new MockBotStore()
    bs.reloadBotFromDiskF = _ => IO.pure(Right(()))

    val app = new ReloadBotEndpoint(bs).routes.orNotFound
    val req = Request[IO](Method.POST, uri"/api/bot/cala/reload").withEntity(Json.obj())
    for {
      resp <- app.run(req)
      body <- resp.as[Json]
    } yield {
      assertEquals(resp.status, Status.Ok)
      assertEquals(body, Json.obj("ok" -> Json.fromBoolean(true), "botId" -> Json.fromString("cala")))
    }
  }

  test("maps Unknown botId to 404") {
    val bs = new MockBotStore()
    bs.reloadBotFromDiskF = _ => IO.pure(Left(ApiError("Unknown botId: x")))

    val app = new ReloadBotEndpoint(bs).routes.orNotFound
    val req = Request[IO](Method.POST, uri"/api/bot/x/reload").withEntity(Json.obj())
    for {
      resp <- app.run(req)
      body <- resp.as[Json]
    } yield {
      assertEquals(resp.status, Status.NotFound)
      assertEquals(body.hcursor.downField("error").as[String].toOption, Some("Unknown botId: x"))
    }
  }

  test("maps other errors to 500") {
    val bs = new MockBotStore()
    bs.reloadBotFromDiskF = _ => IO.pure(Left(ApiError("boom")))

    val app = new ReloadBotEndpoint(bs).routes.orNotFound
    val req = Request[IO](Method.POST, uri"/api/bot/cala/reload").withEntity(Json.obj())
    for {
      resp <- app.run(req)
      body <- resp.as[Json]
    } yield {
      assertEquals(resp.status, Status.InternalServerError)
      assertEquals(body.hcursor.downField("error").as[String].toOption, Some("boom"))
    }
  }
}

