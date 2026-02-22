package com.benkio.XahLeeBot

import cats.effect.*
import com.benkio.telegrambotinfrastructure.SBotMainPolling

object XahLeeBotMainPolling extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    SBotMainPolling.run(sBotInfo = XahLeeBot.sBotInfo)

}
