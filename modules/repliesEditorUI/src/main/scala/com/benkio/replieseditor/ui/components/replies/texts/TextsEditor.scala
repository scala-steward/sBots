package com.benkio.replieseditor.ui.components.replies.texts

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*

object TextsEditor {

  def render(
    editableVar: Var[EditableEntry],
    update: (EditableEntry => EditableEntry) => Unit
  ): Div =
    div(
      div(cls := "fw-semibold mb-1", "Texts"),
      div(
        children <-- editableVar.signal
          .map(_.texts)
          .splitByIndex { (textIdx, _, textSignal) =>
            TextRow.render(
              textIdx = textIdx,
              textSignal = textSignal,
              update = update
            )
          }
      ),
      button(
        cls := "btn btn-sm btn-outline-secondary mb-3",
        "+ text",
        onClick --> { _ =>
          update(e0 => e0.copy(texts = e0.texts :+ ""))
        }
      )
    )
}

