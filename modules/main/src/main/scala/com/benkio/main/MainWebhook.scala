package com.benkio.main

import com.benkio.main.Logger.given
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import org.http4s.server.Server
import telegramium.bots.high.WebhookBot

object MainWebhook extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {

    def server(mainSetup: MainSetup[IO]): Resource[IO, Server] = for {
      bots   <- BotRegistry.value.webhookBots(mainSetup)
      server <- WebhookBot.compose[IO](
        bots = bots,
        port = mainSetup.port,
        host = mainSetup.host,
        keystorePath = mainSetup.keystorePath,
        keystorePassword = mainSetup.keystorePassword
      )
    } yield server

    (for {
      mainSetup <- MainSetup[IO]()
      _         <- GeneralErrorHandling.dbLogAndRestart[IO, Server](mainSetup.dbLayer.dbLog, server(mainSetup))
    } yield ExitCode.Success).useForever

  }
}
