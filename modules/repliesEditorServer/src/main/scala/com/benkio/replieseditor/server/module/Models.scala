package com.benkio.replieseditor.server.module

import io.circe.Encoder
import io.circe.Json
import io.circe.generic.semiauto.deriveEncoder

import java.nio.file.Path

final case class BotFiles(
    botId: String,
    botName: String,
    repliesJson: Path,
    listJson: Path,
    triggersTxt: Path
)

final case class ApiBot(botId: String, botName: String)
object ApiBot {
  given Encoder[ApiBot] = deriveEncoder
}

final case class SaveOk(botId: String, repliesCount: Int)
object SaveOk {
  given Encoder[SaveOk] = deriveEncoder
}

final case class ApiError(error: String, details: Option[Json] = None)
object ApiError {
  given Encoder[ApiError] = deriveEncoder
}

