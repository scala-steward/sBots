package com.benkio.replieseditor.load

import com.benkio.replieseditor.module.*
import io.circe.Json

object RepliesJsonMapping {

  def extractEditableEntry(j: Json): Option[EditableEntry] = {
    val c = j.hcursor
    val matcher = c.downField("matcher").as[String].getOrElse("ContainsOnce")

    val triggers: Option[Vector[TriggerEdit]] =
      for {
        triggerObj <- c.downField("trigger").focus
        textTrigger <- triggerObj.hcursor.downField("TextTrigger").focus
        triggersArr <- textTrigger.hcursor.downField("triggers").as[Vector[Json]].toOption
      } yield triggersArr.flatMap { tj =>
        val tc = tj.hcursor
        tc.downField("StringTextTriggerValue")
          .as[String]
          .toOption
          .map(v => TriggerEdit(TriggerKind.PlainString, v, None))
          .orElse(
            tc.downField("RegexTextTriggerValue")
              .downField("trigger")
              .as[String]
              .toOption
              .map { v =>
                val len = tc.downField("RegexTextTriggerValue").downField("regexLength").as[Int].toOption
                TriggerEdit(TriggerKind.Regex, v, len)
              }
          )
      }

    val files: Option[Vector[String]] =
      for {
        replyObj <- c.downField("reply").focus
        mediaReply <- replyObj.hcursor.downField("MediaReply").focus
        mediaFilesArr <- mediaReply.hcursor.downField("mediaFiles").as[Vector[Json]].toOption
      } yield mediaFilesArr.flatMap { mf =>
        val mc = mf.hcursor
        val fileKinds = List("Mp3File", "GifFile", "VideoFile", "PhotoFile", "Document", "Sticker")
        fileKinds.view.flatMap(k => mc.downField(k).downField("filepath").as[String].toOption).headOption
      }

    for {
      f <- files
      t <- triggers
    } yield EditableEntry(files = f, triggers = t, matcher = matcher)
  }

  private def mediaFileWrapper(filename: String): String =
    if (filename.endsWith(".mp3")) "Mp3File"
    else if (filename.endsWith(".jpg") || filename.endsWith(".png")) "PhotoFile"
    else if (filename.endsWith(".mp4") && filename.endsWith("Gif.mp4")) "GifFile"
    else if (filename.endsWith(".mp4")) "VideoFile"
    else if (filename.endsWith(".sticker")) "Sticker"
    else "Document"

  def buildJsonFromEditable(e: EditableEntry): Either[String, Json] = {
    val triggersJsonE: Either[String, Vector[Json]] = {
      val init: Either[String, Vector[Json]] = Right(Vector.empty)
      e.triggers.foldLeft(init) { (accE, t) =>
        accE.flatMap { acc =>
          t match {
            case TriggerEdit(TriggerKind.PlainString, v, _) =>
              Right(acc :+ Json.obj("StringTextTriggerValue" -> Json.fromString(v)))
            case TriggerEdit(TriggerKind.Regex, v, Some(len)) =>
              Right(
                acc :+ Json.obj(
                  "RegexTextTriggerValue" -> Json.obj(
                    "trigger" -> Json.fromString(v),
                    "regexLength" -> Json.fromInt(len)
                  )
                )
              )
            case TriggerEdit(TriggerKind.Regex, v, None) =>
              Left(s"Regex trigger '$v' is missing regexLength")
          }
        }
      }
    }

    triggersJsonE.map { triggersJson =>
      val mediaFilesJson: Vector[Json] =
        e.files.map { f =>
          val wrapper = mediaFileWrapper(f)
          Json.obj(
            wrapper -> Json.obj(
              "filepath" -> Json.fromString(f),
              "replyToMessage" -> Json.fromBoolean(false)
            )
          )
        }

      Json.obj(
        "trigger" -> Json.obj(
          "TextTrigger" -> Json.obj(
            "triggers" -> Json.fromValues(triggersJson)
          )
        ),
        "reply" -> Json.obj(
          "MediaReply" -> Json.obj(
            "mediaFiles" -> Json.fromValues(mediaFilesJson),
            "replyToMessage" -> Json.fromBoolean(false)
          )
        ),
        "matcher" -> Json.fromString(e.matcher)
      )
    }
  }
}

