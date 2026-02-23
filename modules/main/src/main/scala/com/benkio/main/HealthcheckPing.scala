package com.benkio.main

import cron4s.CronExpr
import org.http4s.client.Client
import org.http4s.Uri

object HealthcheckPing {

  // TODO: Use `cron4s` to schedule a ping every X time.
  // Get the config from outside for the cron.
  // Spawn a non-cancellable fiber
  // Add it to the webhook main
  def healthcheckPing[F[_]](client: Client[F], healthcheckEndpoint: Uri, healthcheckCron: CronExpr): F[Unit] = {
    ???
  }
}
