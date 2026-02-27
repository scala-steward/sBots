package com.benkio.replieseditor.server

import cats.effect.*
import com.benkio.replieseditor.server.server.{ServerBuilder, ServerDeps}

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    ServerDeps.build().flatMap { deps =>
      IO.println(
        s"[RepliesEditor] sbots.repoRoot sysprop = ${Option(System.getProperty("sbots.repoRoot")).getOrElse("<unset>")}"
      ) *>
        IO.println(s"[RepliesEditor] effective repoRoot = ${deps.repoRoot.toString}") *>
        ServerBuilder.build(deps).use(_ => IO.println("Replies editor running on http://127.0.0.1:8088/") *> IO.never)
    }
  }
}

