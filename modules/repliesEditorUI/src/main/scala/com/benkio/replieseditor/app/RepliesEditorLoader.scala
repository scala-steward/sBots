package com.benkio.replieseditor.app

import com.benkio.replieseditor.load.ApiClient
import com.benkio.replieseditor.module.*
import io.circe.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success

final class RepliesEditorLoader(store: RepliesEditorStore) {

  def init(): Unit = loadBots()

  def loadBots(): Unit = {
    store.setStatus("Loading bots…")
    ApiClient.fetchJson("/api/bots").onComplete {
      case Failure(ex)          => store.setStatus(s"Failed to load bots: ${ex.getMessage}")
      case Success(Left(err))   => store.setStatus(s"Failed to load bots: $err")
      case Success(Right(json)) =>
        ApiClient.decodeOrError[Vector[ApiBot]](json) match {
          case Left(err)   => store.setStatus(s"Failed to decode bots: $err")
          case Right(bots) =>
            store.botsVar.set(bots)
            store.clearStatus()
        }
    }
  }

  def loadBot(botId: String): Unit = {
    val myToken = store.loadTokenVar.now() + 1L
    store.loadTokenVar.set(myToken)

    store.setStatus(s"Loading $botId…")
    store.dirtyVar.set(false)
    store.entriesVar.set(Vector.empty)
    store.allowedFilesVar.set(Vector.empty)
    store.totalVar.set(None)
    store.currentPageVar.set(1)
    store.loadingVar.set(false)

    val allowedF = ApiClient.fetchJson(s"/api/bot/$botId/allowed-files")
    allowedF.onComplete {
      case Failure(ex) =>
        if store.loadTokenVar.now() == myToken && store.selectedBotVar.now().contains(botId) then store.setStatus(
          s"Failed to load bot data: ${ex.getMessage}"
        )
      case Success(Left(err)) =>
        if store.loadTokenVar.now() == myToken && store.selectedBotVar.now().contains(botId) then store.setStatus(
          s"Failed to load allowed files: $err"
        )
      case Success(Right(allowedJson)) =>
        if store.loadTokenVar.now() == myToken && store.selectedBotVar.now().contains(botId) then {
          val allowed = ApiClient.decodeOrError[Vector[String]](allowedJson).getOrElse(Vector.empty)
          store.allowedFilesVar.set(allowed)
          loadPage(botId, page = 1)
        }
    }
  }

  def loadPage(botId: String, page: Int): Unit = {
    val myToken   = store.loadTokenVar.now()
    val p         = page.max(1)
    val ps        = store.pageSizeVar.now().max(1)
    val offset    = (p - 1) * ps
    val filterMsg = store.filterTextVar.now().trim

    store.loadingVar.set(true)

    val repliesChunkF =
      if filterMsg.isEmpty then ApiClient.fetchJson(s"/api/bot/$botId/replies-chunk?offset=$offset&limit=$ps")
      else
        ApiClient.postJson(
          s"/api/bot/$botId/replies-filter-chunk?offset=$offset&limit=$ps",
          Json.obj("message" -> Json.fromString(filterMsg))
        )

    repliesChunkF.onComplete {
      case Failure(ex) =>
        if store.loadTokenVar.now() == myToken && store.selectedBotVar.now().contains(botId) then store.setStatus(
          s"Failed to load replies page: ${ex.getMessage}"
        )
        store.loadingVar.set(false)
      case Success(Left(err)) =>
        if store.loadTokenVar.now() == myToken && store.selectedBotVar.now().contains(botId) then store.setStatus(
          s"Failed to load replies page: $err"
        )
        store.loadingVar.set(false)
      case Success(Right(json)) =>
        ApiClient.decodeOrError[RepliesChunk](json) match {
          case Left(err) =>
            if store.loadTokenVar.now() == myToken && store.selectedBotVar.now().contains(botId) then store.setStatus(
              s"Failed to decode replies page: $err"
            )
          case Right(chunk) =>
            if store.loadTokenVar.now() == myToken && store.selectedBotVar.now().contains(botId) then {
              val computedOffset = (p - 1) * ps
              if chunk.items.isEmpty && p > 1 && chunk.total > 0 && computedOffset >= chunk.total then loadPage(
                botId,
                p - 1
              )
              else {
                store.currentPageVar.set(p)
                store.setChunk(chunk)
                store.clearStatus()
              }
            }
        }
        store.loadingVar.set(false)
    }
  }
}
