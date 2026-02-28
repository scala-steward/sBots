package com.benkio.replieseditor.ui.components.replies.triggers

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*

object TriggersEditor {

  def render(
      triggers: Signal[Vector[TriggerEdit]],
      onAddTrigger: () => Unit,
      onRemoveTrigger: Int => Unit,
      onKindChange: (Int, TriggerKind) => Unit,
      onValueChange: (Int, String) => Unit,
      onRegexLenChange: (Int, Option[Int]) => Unit
  ): Div =
    div(
      div(cls := "fw-semibold mb-1", "Triggers"),
      div(
        children <-- triggers.splitByIndex { (triggerIdx, _, triggerSignal) =>
          TriggerRow.render(
            triggerSignal = triggerSignal,
            onRemove = () => onRemoveTrigger(triggerIdx),
            onKindChange = k => onKindChange(triggerIdx, k),
            onValueChange = v => onValueChange(triggerIdx, v),
            onRegexLenChange = v => onRegexLenChange(triggerIdx, v)
          )
        }
      ),
      button(
        cls := "btn btn-sm btn-outline-secondary",
        "+ trigger",
        onClick --> { _ => onAddTrigger() }
      )
    )
}
