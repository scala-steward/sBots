package com.benkio.replieseditor.ui.components.replies

import com.benkio.replieseditor.module.*
import com.benkio.replieseditor.ui.components.replies.files.FilesEditor
import com.benkio.replieseditor.ui.components.replies.triggers.TriggersEditor
import com.raquo.laminar.api.L.*

object ReplyCard {

  def render(
    idx: Int,
    st: EntryState,
    entriesVar: Var[Vector[EntryState]],
    allowedFilesVar: Var[Vector[String]],
    markDirty: () => Unit,
    onEditableChanged: (Int, EditableEntry) => Unit
  ): Div =
    div(
      cls := "col-12 col-md-4",
      div(
        cls := "card shadow-sm",
        div(
          cls := "card-body",
          h6(cls := "card-title", s"Reply #${idx + 1}"),
          st.editable match {
            case None =>
              div(
                cls := "text-muted small",
                "Non-editable entry (kept as-is on save)."
              )
            case Some(editable0) =>
              val editableVar = Var(editable0)

              val syncEditableToOuter: Binder[HtmlElement] =
                editableVar.signal --> { e =>
                  val current = entriesVar.now()
                  val updated = current.updated(idx, current(idx).copy(editable = Some(e)))
                  entriesVar.set(updated)
                }

              def update(f: EditableEntry => EditableEntry): Unit = {
                editableVar.update(f)
                markDirty()
                onEditableChanged(st.index, editableVar.now())
              }

              div(
                syncEditableToOuter,
                FilesEditor.render(
                  idx = idx,
                  editableVar = editableVar,
                  allowedFilesVar = allowedFilesVar,
                  update = update
                ),
                TriggersEditor.render(
                  editableVar = editableVar,
                  update = update
                )
              )
          }
        )
      )
    )
}

