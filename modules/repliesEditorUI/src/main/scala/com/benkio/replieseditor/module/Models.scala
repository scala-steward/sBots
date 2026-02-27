package com.benkio.replieseditor.module

import io.circe.Decoder
import io.circe.Json
import io.circe.generic.semiauto.deriveDecoder

final case class ApiBot(botId: String, botName: String)
object ApiBot {
  given Decoder[ApiBot] = io.circe.generic.semiauto.deriveDecoder
}

enum TriggerKind {
  case PlainString, Regex
}

final case class TriggerEdit(kind: TriggerKind, value: String, regexLength: Option[Int])

final case class EditableEntry(
    files: Vector[String],
    triggers: Vector[TriggerEdit],
    matcher: String
)

final case class EntryState(
    index: Int,
    original: Json,
    editable: Option[EditableEntry]
)

final case class RepliesChunk(total: Int, offset: Int, items: Vector[Json])
object RepliesChunk {
  given Decoder[RepliesChunk] = deriveDecoder
}

