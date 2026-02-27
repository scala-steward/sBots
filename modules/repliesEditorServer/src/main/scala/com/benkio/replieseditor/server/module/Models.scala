package com.benkio.replieseditor.server.module

import io.circe.Encoder
import io.circe.Decoder
import io.circe.Json
import io.circe.generic.semiauto.deriveEncoder
import io.circe.generic.semiauto.deriveDecoder

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

final case class RepliesChunk(total: Int, offset: Int, items: Vector[Json])
object RepliesChunk {
  given Encoder[RepliesChunk] = deriveEncoder
}

final case class UpdateReplyReq(index: Int, value: Json)
object UpdateReplyReq {
  given Decoder[UpdateReplyReq] = deriveDecoder
}

final case class InsertReplyReq(index: Int, value: Json)
object InsertReplyReq {
  given Decoder[InsertReplyReq] = deriveDecoder
}

final case class DeleteReplyReq(index: Int)
object DeleteReplyReq {
  given Decoder[DeleteReplyReq] = deriveDecoder
}

