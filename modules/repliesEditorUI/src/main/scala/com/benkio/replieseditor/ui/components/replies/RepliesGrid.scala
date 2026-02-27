package com.benkio.replieseditor.ui.components.replies

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*

object RepliesGrid {

  def render(
    entriesVar: Var[Vector[EntryState]],
    allowedFilesVar: Var[Vector[String]],
    markDirty: () => Unit,
    onEditableChanged: (Int, EditableEntry) => Unit,
    onDelete: Int => Unit
  ): Div =
    div(
      cls := "row g-3",
      children <-- entriesVar.signal.splitByIndex { (idx, st, _) =>
        ReplyCard.render(
          idx = idx,
          st = st,
          entriesVar = entriesVar,
          allowedFilesVar = allowedFilesVar,
          markDirty = markDirty,
          onEditableChanged = onEditableChanged,
          onDelete = onDelete
        )
      }
    )
}

