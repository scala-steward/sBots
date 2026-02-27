package com.benkio.replieseditor.ui.components.replies.texts

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*

object TextRow {

  def render(
    textIdx: Int,
    textSignal: Signal[String],
    update: (EditableEntry => EditableEntry) => Unit
  ): Div =
    div(
      cls := "input-group input-group-sm mb-1",
      input(
        cls := "form-control",
        typ := "text",
        controlled(
          value <-- textSignal,
          onInput.mapToValue --> { v =>
            update(e0 => e0.copy(texts = e0.texts.updated(textIdx, v)))
          }
        )
      ),
      button(
        cls := "btn btn-outline-danger",
        "−",
        onClick --> { _ =>
          update(e0 => e0.copy(texts = e0.texts.patch(textIdx, Nil, 1)))
        }
      )
    )
}

