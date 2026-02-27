package com.benkio.replieseditor.ui

import com.benkio.replieseditor.load.ApiClient
import com.benkio.replieseditor.load.RepliesJsonMapping
import com.benkio.replieseditor.module.*
import com.benkio.replieseditor.ui.components.page.AppPage
import com.benkio.replieseditor.ui.components.page.PaginationBar
import com.raquo.laminar.api.L.*
import io.circe.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object AppView {

  def render: Div = {
    val botsVar          = Var(Vector.empty[ApiBot])
    val selectedBotVar   = Var(Option.empty[String])
    val allowedFilesVar  = Var(Vector.empty[String])
    val entriesVar       = Var(Vector.empty[EntryState])
    val totalVar         = Var(Option.empty[Int])
    val currentPageVar   = Var(1)
    val pageSizeVar      = Var(60)
    val loadingPageVar   = Var(false)
    val dirtyVar         = Var(false)
    val statusVar        = Var(Option.empty[String])
    val loadTokenVar     = Var(0L)

    def setStatus(msg: String): Unit = statusVar.set(Some(msg))
    def clearStatus(): Unit          = statusVar.set(None)

    def loadBots(): Unit = {
      setStatus("Loading bots…")
      ApiClient.fetchJson("/api/bots").onComplete {
        case Failure(ex) => setStatus(s"Failed to load bots: ${ex.getMessage}")
        case Success(Left(err)) => setStatus(s"Failed to load bots: $err")
        case Success(Right(json)) =>
          ApiClient.decodeOrError[Vector[ApiBot]](json) match {
            case Left(err) => setStatus(s"Failed to decode bots: $err")
            case Right(bots) =>
              botsVar.set(bots)
              clearStatus()
          }
      }
    }

    def setChunk(chunk: RepliesChunk): Unit = {
      val base = chunk.offset
      val states =
        chunk.items.zipWithIndex.map { case (j, i) =>
          EntryState(index = base + i, original = j, editable = RepliesJsonMapping.extractEditableEntry(j))
        }
      totalVar.set(Some(chunk.total))
      entriesVar.set(states)
    }

    def loadPage(botId: String, page: Int, myToken: Long): Unit = {
      val p        = page.max(1)
      val pageSize = pageSizeVar.now().max(1)
      val offset   = (p - 1) * pageSize

      loadingPageVar.set(true)
      val repliesChunkF = ApiClient.fetchJson(s"/api/bot/$botId/replies-chunk?offset=$offset&limit=$pageSize")
      repliesChunkF.onComplete {
        case Failure(ex) =>
          if (loadTokenVar.now() == myToken && selectedBotVar.now().contains(botId))
            setStatus(s"Failed to load replies page: ${ex.getMessage}")
          loadingPageVar.set(false)
        case Success(Left(err)) =>
          if (loadTokenVar.now() == myToken && selectedBotVar.now().contains(botId))
            setStatus(s"Failed to load replies page: $err")
          loadingPageVar.set(false)
        case Success(Right(json)) =>
          ApiClient.decodeOrError[RepliesChunk](json) match {
            case Left(err) =>
              if (loadTokenVar.now() == myToken && selectedBotVar.now().contains(botId))
                setStatus(s"Failed to decode replies page: $err")
            case Right(chunk) =>
              if (loadTokenVar.now() == myToken && selectedBotVar.now().contains(botId)) {
                val computedOffset = (p - 1) * pageSize
                if (chunk.items.isEmpty && p > 1 && chunk.total > 0 && computedOffset >= chunk.total)
                  loadPage(botId, page = p - 1, myToken = myToken)
                else {
                  currentPageVar.set(p)
                  setChunk(chunk)
                  clearStatus()
                }
              }
          }
          loadingPageVar.set(false)
      }
    }

    def loadBot(botId: String): Unit = {
      val myToken = loadTokenVar.now() + 1L
      loadTokenVar.set(myToken)

      setStatus(s"Loading $botId…")
      dirtyVar.set(false)
      entriesVar.set(Vector.empty)
      allowedFilesVar.set(Vector.empty)
      totalVar.set(None)
      currentPageVar.set(1)
      loadingPageVar.set(false)

      val allowedF = ApiClient.fetchJson(s"/api/bot/$botId/allowed-files")
      allowedF.onComplete {
        case Failure(ex) =>
          if (loadTokenVar.now() == myToken && selectedBotVar.now().contains(botId))
            setStatus(s"Failed to load bot data: ${ex.getMessage}")
        case Success(Left(err)) =>
          if (loadTokenVar.now() == myToken && selectedBotVar.now().contains(botId))
            setStatus(s"Failed to load allowed files: $err")
        case Success(Right(allowedJson)) =>
          if (loadTokenVar.now() == myToken && selectedBotVar.now().contains(botId)) {
            val allowed = ApiClient.decodeOrError[Vector[String]](allowedJson).getOrElse(Vector.empty)
            allowedFilesVar.set(allowed)
            loadPage(botId, page = 1, myToken = myToken)
          }
      }
    }

    def markDirty(): Unit = dirtyVar.set(true)

    def pushUpdate(botId: String, index: Int, e: EditableEntry): Unit = {
      RepliesJsonMapping.buildJsonFromEditable(e) match {
        case Left(err) =>
          setStatus(s"Invalid entry #${index + 1}: $err")
        case Right(j) =>
          val payload = Json.obj(
            "index" -> Json.fromInt(index),
            "value" -> j
          )
          ApiClient.postJson(s"/api/bot/$botId/replies/update", payload).onComplete {
            case Failure(ex) => setStatus(s"Update failed: ${ex.getMessage}")
            case Success(Left(err)) => setStatus(s"Update failed: $err")
            case Success(Right(_))  => ()
          }
      }
    }

    def deleteEntry(botId: String, index: Int): Unit = {
      setStatus(s"Deleting reply #${index + 1}…")
      val payload = Json.obj("index" -> Json.fromInt(index))
      ApiClient.postJson(s"/api/bot/$botId/replies/delete", payload).onComplete {
        case Failure(ex) => setStatus(s"Delete failed: ${ex.getMessage}")
        case Success(Left(err)) => setStatus(s"Delete failed: $err")
        case Success(Right(_)) =>
          dirtyVar.set(true)
          loadPage(botId, page = currentPageVar.now(), myToken = loadTokenVar.now())
      }
    }

    def addNewAtCurrentPageTop(botId: String): Unit = {
      val firstAllowed = allowedFilesVar.now().headOption.getOrElse("")
      val newEntry =
        EditableEntry(
          files = if (firstAllowed.isEmpty) Vector.empty else Vector(firstAllowed),
          triggers = Vector(TriggerEdit(TriggerKind.PlainString, "", None)),
          matcher = "ContainsOnce"
        )
      RepliesJsonMapping.buildJsonFromEditable(newEntry) match {
        case Left(err) => setStatus(s"Cannot add: $err")
        case Right(j) =>
          setStatus("Adding reply…")
          val total    = totalVar.now().getOrElse(0)
          val pageSize = pageSizeVar.now().max(1)
          val page     = currentPageVar.now().max(1)
          val offset   = ((page - 1) * pageSize).min(total).max(0)
          val payload = Json.obj(
            "index" -> Json.fromInt(offset),
            "value" -> j
          )
          ApiClient.postJson(s"/api/bot/$botId/replies/insert", payload).onComplete {
            case Failure(ex) => setStatus(s"Add failed: ${ex.getMessage}")
            case Success(Left(err)) => setStatus(s"Add failed: $err")
            case Success(Right(respJson)) =>
              val newTotal =
                respJson.hcursor.downField("total").as[Int].toOption.orElse(totalVar.now()).getOrElse(total + 1)
              dirtyVar.set(true)
              totalVar.set(Some(newTotal))
              loadPage(botId, page = page, myToken = loadTokenVar.now())
          }
      }
    }

    def commit(botId: String): Unit = {
      setStatus("Saving…")
      ApiClient.postJson(s"/api/bot/$botId/replies/commit", Json.obj()).onComplete {
        case Failure(ex) => setStatus(s"Save failed: ${ex.getMessage}")
        case Success(Left(err)) => setStatus(s"Save failed: $err")
        case Success(Right(_)) =>
          dirtyVar.set(false)
          setStatus("Saved.")
      }
    }

    AppPage.render(
      bots = botsVar.signal,
      selectedBotVar = selectedBotVar,
      dirty = dirtyVar.signal,
      status = statusVar.signal,
      entriesVar = entriesVar,
      allowedFilesVar = allowedFilesVar,
      paginationBar = PaginationBar.render(
        currentPageVar = currentPageVar,
        totalOpt = totalVar.signal,
        pageSizeVar = pageSizeVar,
        isLoading = loadingPageVar.signal,
        onPageRequested = { page =>
          selectedBotVar.now().foreach(botId => loadPage(botId, page = page, myToken = loadTokenVar.now()))
        }
      ),
      onMount = () => loadBots(),
      onBotSelected = { botIdOpt =>
        selectedBotVar.set(botIdOpt)
        botIdOpt.foreach(loadBot)
      },
      onReload = () => selectedBotVar.now().foreach(loadBot),
      onAddNew = () => selectedBotVar.now().foreach(addNewAtCurrentPageTop),
      onSave = () => selectedBotVar.now().foreach(commit),
      onEditableChanged = (index, e) => selectedBotVar.now().foreach(botId => pushUpdate(botId, index, e)),
      onDelete = (index: Int) => selectedBotVar.now().foreach(botId => deleteEntry(botId, index)),
      markDirty = () => markDirty()
    )
  }
}

