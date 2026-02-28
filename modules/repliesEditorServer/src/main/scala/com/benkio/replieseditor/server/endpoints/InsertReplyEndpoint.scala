package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.module.InsertReplyReq
import com.benkio.replieseditor.server.store.BotStoreApi
import io.circe.syntax.*
import io.circe.Json
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.io.*

final class InsertReplyEndpoint(botStore: BotStoreApi) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "api" / "bot" / botId / "replies" / "insert" =>
      for {
        bodyJson <- req.as[Json]
        ins      <- bodyJson.as[InsertReplyReq] match {
          case Left(df) => IO.raiseError(new RuntimeException(df.message))
          case Right(i) => IO.pure(i)
        }
        res <- botStore.insertAt(botId, ins.index, ins.value).flatMap {
          case Left(err) =>
            if err.error.startsWith("Unknown botId") then NotFound(err.asJson)
            else BadRequest(err.asJson)
          case Right(newTotal) =>
            Ok(Json.obj("ok" -> Json.fromBoolean(true), "total" -> Json.fromInt(newTotal)).asJson)
        }
      } yield res
  }
}
