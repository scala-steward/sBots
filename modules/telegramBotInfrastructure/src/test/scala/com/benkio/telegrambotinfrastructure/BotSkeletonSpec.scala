package com.benkio.telegrambotinfrastructure

import com.benkio.telegrambotinfrastructure.model.CommandTrigger
import com.benkio.telegrambotinfrastructure.model.reply.TextReply
import com.benkio.telegrambotinfrastructure.model.reply.ReplyBundleCommand
import com.benkio.telegrambotinfrastructure.model.reply.ReplyBundleMessage
import cats.effect.IO
import cats.implicits.*
import com.benkio.telegrambotinfrastructure.mocks.ApiMock.given
import com.benkio.telegrambotinfrastructure.mocks.SampleWebhookBot
import com.benkio.telegrambotinfrastructure.model.reply.ReplyBundle
import java.time.Instant
import log.effect.LogLevels
import log.effect.LogWriter
import log.effect.fs2.SyncLogWriter.consoleLogUpToLevel
import munit.CatsEffectSuite
import telegramium.bots.Chat
import telegramium.bots.Message
import com.benkio.telegrambotinfrastructure.model.reply.mp3
import com.benkio.telegrambotinfrastructure.model.reply.vid
import com.benkio.telegrambotinfrastructure.model.reply.gif
import com.benkio.telegrambotinfrastructure.model.tr

class BotSkeletonSpec extends CatsEffectSuite {

  given log: LogWriter[IO] = consoleLogUpToLevel(LogLevels.Info)

  test("selectReplyBundle should return all the expected `ReplyBundleMessage` respecting the trigger ordering") {
    val inputMessage: Message = Message(
      messageId = 0,
      date = Instant.now.getEpochSecond().toInt,
      chat = Chat(id = 0, `type` = "test"),
      text = Some(s"carne dura")
    )
    val expected = ReplyBundleMessage
      .textToMedia[IO](
        "carne (dura|vecchia|fresca)".r.tr(10),
      )(
        mp3"rphjb_CarneFrescaSaporita.mp3",
        vid"rphjb_CarneFrescaSaporita.mp4",
        gif"rphjb_CarneFrescaSaporitaGif.mp4"
      )

    for
      sampleWebhookBot <- SampleWebhookBot()
      resultOpt        <- sampleWebhookBot.selectReplyBundle(inputMessage)
      result <- resultOpt.fold(Throwable("BotSkeletonSpec expected Some, got None").raiseError[IO, String]) {
        _.prettyPrint()
      }
      expectedPP <- expected.prettyPrint()
    yield assertEquals(result, expectedPP)
  }

  test("selectCommandReplyBundle should return all the expected `ReplyBundleCommand`") {
    val inputMessage: Message = Message(
      messageId = 0,
      date = Instant.now.getEpochSecond().toInt,
      chat = Chat(id = 0, `type` = "test"),
      text = Some(s"/testcommand")
    )
    val expected =
      ReplyBundleCommand(
        trigger = CommandTrigger("testcommand"),
        reply = TextReply.fromList[IO](
          "test command reply"
        )(false)
      )

    for
      sampleWebhookBot <- SampleWebhookBot()
      resultOpt        <- sampleWebhookBot.selectCommandReplyBundle(inputMessage)
      result <- resultOpt.fold(Throwable("BotSkeletonSpec expected Some, got None").raiseError[IO, String]) {
        _.prettyPrint()
      }
      expectedPP <- expected.prettyPrint()
    yield assertEquals(result, expectedPP)
  }
}
