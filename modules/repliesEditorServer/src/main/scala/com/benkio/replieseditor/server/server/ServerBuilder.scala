package com.benkio.replieseditor.server.server

import cats.effect.IO
import cats.effect.Resource
import com.comcast.ip4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server

object ServerBuilder {
  def build(deps: ServerDeps): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(host"127.0.0.1")
      .withPort(port"8088")
      .withHttpApp(HttpAppBuilder.build(deps))
      .build
}

