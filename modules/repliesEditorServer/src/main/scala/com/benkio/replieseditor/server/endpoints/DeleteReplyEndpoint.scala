package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.module.DeleteReplyReq
import com.benkio.replieseditor.server.store.BotStore
import io.circe.Json
import io.circe.syntax.*
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.io.*

final class DeleteReplyEndpoint(botStore: BotStore) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] { case req @ POST -> Root / "api" / "bot" / botId / "replies" / "delete" =>
    for {
      bodyJson <- req.as[Json]
      del <- bodyJson.as[DeleteReplyReq] match {
        case Left(df) => IO.raiseError(new RuntimeException(df.message))
        case Right(d) => IO.pure(d)
      }
      res <- botStore.deleteAt(botId, del.index).flatMap {
        case Left(err) =>
          if (err.error.startsWith("Unknown botId")) NotFound(err.asJson)
          else BadRequest(err.asJson)
        case Right(newTotal) =>
          Ok(Json.obj("ok" -> Json.fromBoolean(true), "total" -> Json.fromInt(newTotal)).asJson)
      }
    } yield res
  }
}

