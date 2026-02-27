package com.benkio.replieseditor.ui.components.replies.triggers

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*

object TriggersEditor {

  def render(
    editableVar: Var[EditableEntry],
    update: (EditableEntry => EditableEntry) => Unit
  ): Div =
    div(
      div(cls := "fw-semibold mb-1", "Triggers"),
      div(
        children <-- editableVar.signal
          .map(_.triggers)
          .splitByIndex { (triggerIdx, _, triggerSignal) =>
            TriggerRow.render(
              triggerIdx = triggerIdx,
              triggerSignal = triggerSignal,
              update = update
            )
          }
      ),
      button(
        cls := "btn btn-sm btn-outline-secondary",
        "+ trigger",
        onClick --> { _ =>
          update(e0 =>
            e0.copy(triggers = e0.triggers :+ TriggerEdit(TriggerKind.PlainString, "", None))
          )
        }
      )
    )
}

