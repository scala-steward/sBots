package com.benkio.replieseditor.server.server

import cats.effect.IO
import com.benkio.replieseditor.server.endpoints.*
import org.http4s.server.middleware.Logger
import org.http4s.server.Router
import org.http4s.HttpApp

object HttpAppBuilder {
  def build(deps: ServerDeps): HttpApp[IO] = {
    val botsEndpoint                 = new BotsEndpoint(deps.botStore)
    val repliesEndpoint              = new RepliesEndpoint(deps.botStore)
    val repliesChunkEndpoint         = new RepliesChunkEndpoint(deps.botStore)
    val filteredRepliesChunkEndpoint = new FilteredRepliesChunkEndpoint(deps.botStore)
    val allowedFilesEndpoint         = new AllowedFilesEndpoint(deps.botStore)
    val saveRepliesEndpoint          = new SaveRepliesEndpoint(deps.botStore)
    val updateReplyEndpoint          = new UpdateReplyEndpoint(deps.botStore)
    val commitRepliesEndpoint        = new CommitRepliesEndpoint(deps.botStore)
    val insertReplyEndpoint          = new InsertReplyEndpoint(deps.botStore)
    val deleteReplyEndpoint          = new DeleteReplyEndpoint(deps.botStore)

    val routes =
      Router(
        "/" -> StaticAssetsEndpoint.routes,
        "/" -> botsEndpoint.routes,
        "/" -> repliesEndpoint.routes,
        "/" -> repliesChunkEndpoint.routes,
        "/" -> filteredRepliesChunkEndpoint.routes,
        "/" -> allowedFilesEndpoint.routes,
        "/" -> saveRepliesEndpoint.routes,
        "/" -> updateReplyEndpoint.routes,
        "/" -> commitRepliesEndpoint.routes,
        "/" -> insertReplyEndpoint.routes,
        "/" -> deleteReplyEndpoint.routes
      )

    Logger.httpApp(logHeaders = true, logBody = false)(routes.orNotFound)
  }
}
