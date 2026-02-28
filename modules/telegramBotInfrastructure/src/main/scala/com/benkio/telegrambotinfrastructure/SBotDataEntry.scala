package com.benkio.telegrambotinfrastructure

import cats.effect.*
import com.benkio.telegrambotinfrastructure.config.SBotConfig
import com.benkio.telegrambotinfrastructure.dataentry.DataEntry

object SBotDataEntry {

  def run(args: List[String], sBotConfig: SBotConfig): IO[Unit] =
    DataEntry
      .dataEntryLogic(args, sBotConfig)

}
