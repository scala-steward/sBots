package com.benkio.replieseditor.server.validation

import com.benkio.replieseditor.server.module.ApiError
import com.benkio.telegrambotinfrastructure.model.reply.ReplyBundleMessage
import io.circe.syntax.*
import io.circe.Json

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
      test = invalid.isEmpty,
      right = (),
      left = ApiError(
        error = "Some media files are not present in *_list.json",
        details = Some(Json.obj("invalidFiles" -> invalid.asJson))
      )
    )
  }
}
