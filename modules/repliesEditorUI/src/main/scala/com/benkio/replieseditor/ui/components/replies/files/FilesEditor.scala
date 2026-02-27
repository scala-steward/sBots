package com.benkio.replieseditor.ui.components.replies.files

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*

object FilesEditor {

  def render(
    idx: Int,
    editableVar: Var[EditableEntry],
    allowedFilesVar: Var[Vector[String]],
    update: (EditableEntry => EditableEntry) => Unit
  ): Div =
    div(
      div(cls := "fw-semibold mb-1", "Files"),
      div(
        children <-- editableVar.signal
          .map(_.files)
          .splitByIndex { (fileIdx, _, fileSignal) =>
            FileRow.render(
              entryIdx = idx,
              fileIdx = fileIdx,
              fileSignal = fileSignal,
              allowedFilesVar = allowedFilesVar,
              update = update
            )
          }
      ),
      button(
        cls := "btn btn-sm btn-outline-secondary mb-3",
        "+ file",
        onClick --> { _ =>
          val first = allowedFilesVar.now().headOption.getOrElse("")
          update(e0 => e0.copy(files = e0.files :+ first))
        }
      )
    )
}

