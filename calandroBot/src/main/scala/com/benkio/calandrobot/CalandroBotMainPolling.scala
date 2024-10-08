package com.benkio.calandrobot

import cats.effect.*
import log.effect.fs2.SyncLogWriter.consoleLogUpToLevel
import log.effect.LogLevel
import log.effect.LogLevels
import log.effect.LogWriter

object CalandroBotMainPolling extends IOApp {

  private def internalRun(logLevel: LogLevel): cats.effect.IO[cats.effect.ExitCode] = {
    given log: LogWriter[IO] = consoleLogUpToLevel(logLevel)
    CalandroBot
      .buildPollingBot((cb: CalandroBotPolling[IO]) => cb.start())
      .as(ExitCode.Success)
  }

  def run(args: List[String]): IO[ExitCode] =
    internalRun(LogLevels.Info)

}
