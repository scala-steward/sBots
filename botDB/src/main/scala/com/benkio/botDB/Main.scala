package com.benkio.botDB

import cats.effect._
import com.benkio.botDB.db.BotDBController
import com.benkio.botDB.db.DBMigrator
import com.benkio.botDB.db.DatabaseRepository
import com.benkio.telegrambotinfrastructure.botcapabilities.ResourceAccess

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    (for {
      _   <- IO(println(s"Migrating database configuration"))
      cfg <- Config.loadConfig
      transactor         = Config.buildTransactor(cfg = cfg)
      databaseRepository = DatabaseRepository[IO](transactor)
      resourceAccess     = ResourceAccess.fromResources[IO]
      migrator           = DBMigrator[IO]
      botDBController = BotDBController[IO](
        cfg = cfg,
        databaseRepository = databaseRepository,
        resourceAccess = resourceAccess,
        migrator = migrator
      )
      _ <- botDBController.build.use_
    } yield ()).as(ExitCode.Success)
}