package com.benkio.replieseditor.server.validation

import com.benkio.replieseditor.server.module.ApiError
import com.benkio.telegrambotinfrastructure.model.reply.ReplyBundleMessage
import io.circe.Json
import io.circe.syntax.*

object MediaFilesAllowedValidation {

  def validateAllFilesAreAllowed(
      replies: List[ReplyBundleMessage],
      allowed: Set[String]
  ): Either[ApiError, Unit] = {
    val invalid =
      replies
        .flatMap(_.getMediaFiles)
        .map(_.filepath)
        .distinct
        .filterNot(allowed.contains)

    Either.cond(
      invalid.isEmpty,
      (),
      ApiError(
        error = "Some media files are not present in *_list.json",
        details = Some(Json.obj("invalidFiles" -> invalid.asJson))
      )
    )
  }
}

