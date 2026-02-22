package com.benkio.main

import cats.effect.IO
import log.effect.fs2.SyncLogWriter.consoleLogUpToLevel
import log.effect.LogLevels
import log.effect.LogWriter

object Logger {
  given logWriter: LogWriter[IO] = consoleLogUpToLevel(LogLevels.Info)
}
