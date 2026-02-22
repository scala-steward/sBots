package com.benkio.telegrambotinfrastructure

import cats.effect.*
import com.benkio.telegrambotinfrastructure.model.SBotInfo
import com.benkio.telegrambotinfrastructure.Logger.given
import com.benkio.telegrambotinfrastructure.SBot
import com.benkio.telegrambotinfrastructure.SBotPolling

object SBotMainPolling {

  def run(sBotInfo: SBotInfo): cats.effect.IO[cats.effect.ExitCode] = {
    SBot
      .buildPollingBot((cb: SBotPolling[IO]) => cb.start(), sBotInfo)
      .as(ExitCode.Success)
  }

}
