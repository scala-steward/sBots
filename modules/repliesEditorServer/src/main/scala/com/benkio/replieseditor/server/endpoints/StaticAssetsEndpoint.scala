package com.benkio.replieseditor.server.endpoints

import cats.effect.IO
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.StaticFile

object StaticAssetsEndpoint {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ GET -> Root =>
      StaticFile
        .fromResource("/public/index.html", Some(req))
        .getOrElseF(NotFound())

    case req @ GET -> Root / "public" / fileName =>
      StaticFile
        .fromResource(s"/public/$fileName", Some(req))
        .getOrElseF(NotFound())
  }
}
