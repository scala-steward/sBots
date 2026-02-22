package com.benkio.PinoScottoBot

import cats.effect.*
import com.benkio.telegrambotinfrastructure.SBotMainPolling

object PinoScottoBotMainPolling extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    SBotMainPolling.run(sBotInfo = PinoScottoBot.sBotInfo)

}
