package com.benkio.abarberobot

import cats.effect._

import scala.concurrent.ExecutionContext.Implicits.global

object ABarberoBotMainPolling extends IOApp {
  def run(args: List[String]): IO[cats.effect.ExitCode] =
    ABarberoBot
      .buildPollingBot[IO, Unit](global, (ab: ABarberoBotPolling[IO]) => ab.start())
      .as(ExitCode.Success)
}