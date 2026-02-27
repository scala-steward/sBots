package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.store.BotStore
import io.circe.syntax.*
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.io.*

final class CommitRepliesEndpoint(botStore: BotStore) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] { case POST -> Root / "api" / "bot" / botId / "replies" / "commit" =>
    botStore.commit(botId).flatMap {
      case Left(err) =>
        if (err.error.startsWith("Unknown botId")) NotFound(err.asJson)
        else if (err.error.startsWith("Some media files are not present")) BadRequest(err.asJson)
        else InternalServerError(err.asJson)
      case Right(ok) =>
        Ok(ok.asJson)
    }
  }
}

