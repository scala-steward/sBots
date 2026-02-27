package com.benkio.replieseditor.ui

import com.benkio.replieseditor.load.ApiClient
import com.benkio.replieseditor.load.RepliesJsonMapping
import com.benkio.replieseditor.module.*
import com.benkio.replieseditor.ui.components.page.AppPage
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

    def loadBot(botId: String): Unit = {
      val myToken = loadTokenVar.now() + 1L
      loadTokenVar.set(myToken)

      setStatus(s"Loading $botId…")
      dirtyVar.set(false)
      entriesVar.set(Vector.empty)
      allowedFilesVar.set(Vector.empty)

      val repliesF = ApiClient.fetchJson(s"/api/bot/$botId/replies")
      val allowedF = ApiClient.fetchJson(s"/api/bot/$botId/allowed-files")

      repliesF.zip(allowedF).onComplete {
        case Failure(ex) =>
          if (loadTokenVar.now() == myToken && selectedBotVar.now().contains(botId))
            setStatus(s"Failed to load bot data: ${ex.getMessage}")
        case Success((Left(err), _)) =>
          if (loadTokenVar.now() == myToken && selectedBotVar.now().contains(botId))
            setStatus(s"Failed to load replies: $err")
        case Success((_, Left(err))) =>
          if (loadTokenVar.now() == myToken && selectedBotVar.now().contains(botId))
            setStatus(s"Failed to load allowed files: $err")
        case Success((Right(repliesJson), Right(allowedJson))) =>
          if (loadTokenVar.now() == myToken && selectedBotVar.now().contains(botId)) {
            val allowed = ApiClient.decodeOrError[Vector[String]](allowedJson).getOrElse(Vector.empty)
            allowedFilesVar.set(allowed)

            val entries = repliesJson.asArray match {
              case None => Vector.empty[EntryState]
              case Some(arr) =>
                arr.toVector.map { j =>
                  EntryState(original = j, editable = RepliesJsonMapping.extractEditableEntry(j))
                }
            }
            entriesVar.set(entries)
            clearStatus()
          }
      }
    }

    def markDirty(): Unit = dirtyVar.set(true)

    def save(botId: String): Unit = {
      val entries = entriesVar.now()
      val rebuiltE: Either[String, Vector[Json]] =
        entries.foldLeft(Right(Vector.empty): Either[String, Vector[Json]]) { (accE, st) =>
          accE.flatMap { acc =>
            st.editable match {
              case None => Right(acc :+ st.original)
              case Some(e) =>
                RepliesJsonMapping.buildJsonFromEditable(e).map(j => acc :+ j)
            }
          }
        }

      rebuiltE match {
        case Left(err) =>
          setStatus(s"Cannot save: $err")
        case Right(arr) =>
          setStatus("Saving…")
          ApiClient.postJson(s"/api/bot/$botId/replies", Json.fromValues(arr)).onComplete {
            case Failure(ex) => setStatus(s"Save failed: ${ex.getMessage}")
            case Success(Left(err)) => setStatus(s"Save failed: $err")
            case Success(Right(_)) =>
              dirtyVar.set(false)
              setStatus("Saved.")
          }
      }
    }

    AppPage.render(
      bots = botsVar.signal,
      selectedBotVar = selectedBotVar,
      dirty = dirtyVar.signal,
      status = statusVar.signal,
      entriesVar = entriesVar,
      allowedFilesVar = allowedFilesVar,
      onMount = () => loadBots(),
      onBotSelected = { botIdOpt =>
        selectedBotVar.set(botIdOpt)
        botIdOpt.foreach(loadBot)
      },
      onReload = () => selectedBotVar.now().foreach(loadBot),
      onSave = () => selectedBotVar.now().foreach(save),
      markDirty = () => markDirty()
    )
  }
}

