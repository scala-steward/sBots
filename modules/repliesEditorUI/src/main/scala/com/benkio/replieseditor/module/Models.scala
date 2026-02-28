package com.benkio.replieseditor.module

import io.circe.generic.semiauto.deriveDecoder
import io.circe.Decoder
import io.circe.Json

final case class ApiBot(botId: String, botName: String)
object ApiBot {
  given Decoder[ApiBot] = io.circe.generic.semiauto.deriveDecoder
}

enum TriggerKind {
  case PlainString, Regex
}

final case class TriggerEdit(kind: TriggerKind, value: String, regexLength: Option[Int])

enum ReplyItemKind {
  case File, Text
}

final case class ReplyItem(kind: ReplyItemKind, value: String)

final case class EditableEntry(
    replies: Vector[ReplyItem],
    triggers: Vector[TriggerEdit],
    matcher: String
)

final case class EntryState(
    index: Int,
    original: Json,
    editable: Option[EditableEntry]
)

final case class IndexedReply(index: Int, value: Json)
object IndexedReply {
  given Decoder[IndexedReply] = deriveDecoder
}

final case class RepliesChunk(total: Int, offset: Int, items: Vector[IndexedReply])
object RepliesChunk {
  given Decoder[RepliesChunk] = deriveDecoder
}
