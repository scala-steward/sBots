package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.module.{ApiError, UpdateReplyReq}
import com.benkio.replieseditor.server.store.BotStore
import io.circe.Json
import io.circe.syntax.*
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.io.*

final class UpdateReplyEndpoint(botStore: BotStore) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] { case req @ POST -> Root / "api" / "bot" / botId / "replies" / "update" =>
    for {
      bodyJson <- req.as[Json]
      upd <- bodyJson.as[UpdateReplyReq] match {
        case Left(df) => IO.raiseError(new RuntimeException(df.message))
        case Right(u) => IO.pure(u)
      }
      res <- botStore.updateReplyAt(botId, upd.index, upd.value).flatMap {
        case Left(err) =>
          if (err.error.startsWith("Unknown botId")) NotFound(err.asJson)
          else BadRequest(err.asJson)
        case Right(_) => Ok(Json.obj("ok" -> Json.fromBoolean(true)).asJson)
      }
    } yield res
  }
}

