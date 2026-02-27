package com.benkio.replieseditor.server.server

import cats.effect.IO
import com.benkio.replieseditor.server.endpoints.*
import org.http4s.HttpApp
import org.http4s.server.Router
import org.http4s.server.middleware.Logger

object HttpAppBuilder {
  def build(deps: ServerDeps): HttpApp[IO] = {
    val botsEndpoint         = new BotsEndpoint(deps.botStore)
    val repliesEndpoint      = new RepliesEndpoint(deps.botStore)
    val repliesChunkEndpoint = new RepliesChunkEndpoint(deps.botStore)
    val allowedFilesEndpoint = new AllowedFilesEndpoint(deps.botStore)
    val saveRepliesEndpoint  = new SaveRepliesEndpoint(deps.botStore)
    val updateReplyEndpoint  = new UpdateReplyEndpoint(deps.botStore)
    val commitRepliesEndpoint = new CommitRepliesEndpoint(deps.botStore)

    val routes =
      Router(
        "/" -> StaticAssetsEndpoint.routes,
        "/" -> botsEndpoint.routes,
        "/" -> repliesEndpoint.routes,
        "/" -> repliesChunkEndpoint.routes,
        "/" -> allowedFilesEndpoint.routes,
        "/" -> saveRepliesEndpoint.routes,
        "/" -> updateReplyEndpoint.routes,
        "/" -> commitRepliesEndpoint.routes
      )

    Logger.httpApp(logHeaders = true, logBody = false)(routes.orNotFound)
  }
}

