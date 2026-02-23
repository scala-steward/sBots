package com.benkio.main

import cats.effect.implicits.*
import cats.effect.Async
import cats.implicits.*
import cron4s.CronExpr
import eu.timepit.fs2cron.cron4s.Cron4sScheduler
import fs2.Stream
import org.http4s.client.Client
import org.http4s.Request
import org.http4s.Uri

object HealthcheckPing {

  def healthcheckPing[F[_]: Async](client: Client[F], healthcheckEndpoint: Uri, healthcheckCron: CronExpr): F[Unit] = {
    val cronScheduler     = Cron4sScheduler.systemDefault[F]
    val healthcheckStream =
      cronScheduler.awakeEvery(healthcheckCron) >>
        Stream.eval(sendHealthcheckEndpoint(client, healthcheckEndpoint).void)
    healthcheckStream.compile.drain.start.void
  }

  def sendHealthcheckEndpoint[F[_]](client: Client[F], healthcheckEndpoint: Uri): F[Boolean] =
    client.successful(Request(uri = healthcheckEndpoint))
}
