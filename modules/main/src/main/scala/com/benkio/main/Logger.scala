package com.benkio.main

import log.effect.LogLevels
import log.effect.LogWriter
import cats.effect.IO
import log.effect.fs2.SyncLogWriter.consoleLogUpToLevel

object Logger {
  given logWriter: LogWriter[IO] = consoleLogUpToLevel(LogLevels.Info)
}
