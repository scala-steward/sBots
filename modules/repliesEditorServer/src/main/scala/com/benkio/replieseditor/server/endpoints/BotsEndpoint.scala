package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import com.benkio.replieseditor.server.module.ApiBot
import com.benkio.replieseditor.server.store.BotStore
import io.circe.syntax.*
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.io.*

final class BotsEndpoint(botStore: BotStore) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root / "api" / "bots" =>
    botStore.listBots.flatMap(bots => Ok(bots.asJson))
  }
}

