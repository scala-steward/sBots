package com.benkio.CalandroBot

import cats.effect.*
import com.benkio.telegrambotinfrastructure.SBotMainPolling

object CalandroBotMainPolling extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    SBotMainPolling.run(sBotInfo = CalandroBot.sBotInfo)

}
