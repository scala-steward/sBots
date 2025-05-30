package com.benkio.youtuboanchei0bot.data

import cats.Applicative
import com.benkio.telegrambotinfrastructure.model.reply.pho
import com.benkio.telegrambotinfrastructure.model.reply.ReplyBundleMessage

object Photo:

  def messageRepliesPhotoData[
      F[_]: Applicative
  ]: List[ReplyBundleMessage[F]] = List(
    ReplyBundleMessage
      .textToMedia[F](
        "torta"
      )(
        pho"ytai_SorrisoTortaFelice.jpg"
      )
  )
