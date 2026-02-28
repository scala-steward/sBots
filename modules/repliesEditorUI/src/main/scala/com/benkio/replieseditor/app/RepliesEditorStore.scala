package com.benkio.replieseditor.app

import com.benkio.replieseditor.module.*
import com.raquo.airstream.core.Signal
import com.raquo.airstream.state.Var

final class RepliesEditorStore {
  // state (Vars)
  val botsVar: Var[Vector[ApiBot]]              = Var(Vector.empty)
  val selectedBotVar: Var[Option[String]]       = Var(None)
  val allowedFilesVar: Var[Vector[String]]      = Var(Vector.empty)
  val entriesVar: Var[Vector[EntryState]]       = Var(Vector.empty)

  val totalVar: Var[Option[Int]]                = Var(None)
  val currentPageVar: Var[Int]                  = Var(1)
  val pageSizeVar: Var[Int]                     = Var(60)
  val loadingVar: Var[Boolean]                  = Var(false)

  val dirtyVar: Var[Boolean]                    = Var(false)
  val statusVar: Var[Option[String]]            = Var(None)

  val filtersOpenVar: Var[Boolean]              = Var(false)
  val filterTextVar: Var[String]                = Var("")

  val loadTokenVar: Var[Long]                   = Var(0L)

  // derived signals
  val bots: Signal[Vector[ApiBot]]              = botsVar.signal
  val selectedBotId: Signal[Option[String]]     = selectedBotVar.signal
  val allowedFiles: Signal[Vector[String]]      = allowedFilesVar.signal
  val entries: Signal[Vector[EntryState]]       = entriesVar.signal

  val totalOpt: Signal[Option[Int]]             = totalVar.signal
  val currentPage: Signal[Int]                  = currentPageVar.signal
  val pageSize: Signal[Int]                     = pageSizeVar.signal
  val isLoading: Signal[Boolean]                = loadingVar.signal

  val dirty: Signal[Boolean]                    = dirtyVar.signal
  val status: Signal[Option[String]]            = statusVar.signal

  val filtersOpen: Signal[Boolean]              = filtersOpenVar.signal
  val filterText: Signal[String]                = filterTextVar.signal
  val addDisabled: Signal[Boolean]              = filterTextVar.signal.map(_.trim.nonEmpty)

  // helpers
  def setStatus(msg: String): Unit = statusVar.set(Some(msg))
  def clearStatus(): Unit          = statusVar.set(None)
  def markDirty(): Unit            = dirtyVar.set(true)

  def setChunk(chunk: RepliesChunk): Unit = {
    val states =
      chunk.items.map { item =>
        EntryState(
          index = item.index,
          original = item.value,
          editable = com.benkio.replieseditor.load.RepliesJsonMapping.extractEditableEntry(item.value)
        )
      }
    totalVar.set(Some(chunk.total))
    entriesVar.set(states)
  }
}

