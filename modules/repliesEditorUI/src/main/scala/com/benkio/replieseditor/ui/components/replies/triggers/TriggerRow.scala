package com.benkio.replieseditor.ui.components.replies.triggers

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*

object TriggerRow {

  def render(
      triggerSignal: Signal[TriggerEdit],
      onRemove: () => Unit,
      onKindChange: TriggerKind => Unit,
      onValueChange: String => Unit,
      onRegexLenChange: Option[Int] => Unit
  ): Div = {
    val kindSignal  = triggerSignal.map(_.kind)
    val valueSignal = triggerSignal.map(_.value)
    val lenSignal   = triggerSignal.map(_.regexLength.map(_.toString).getOrElse(""))

    div(
      cls := "d-flex gap-1 align-items-center mb-1",
      select(
        cls := "form-select form-select-sm",
        value <-- kindSignal.map(k => if k == TriggerKind.Regex then "regex" else "string"),
        inContext { thisNode =>
          onChange.mapTo(thisNode.ref.value) --> { v =>
            val k = if v == "regex" then TriggerKind.Regex else TriggerKind.PlainString
            onKindChange(k)
          }
        },
        option(value := "string", "String"),
        option(value := "regex", "Regex")
      ),
      input(
        cls         := "form-control form-control-sm",
        placeholder := "trigger",
        controlled(
          value <-- valueSignal,
          onInput.mapToValue --> onValueChange
        )
      ),
      input(
        cls         := "form-control form-control-sm",
        width       := "6rem",
        placeholder := "len",
        display <-- kindSignal.map(k => if k == TriggerKind.Regex then "" else "none"),
        controlled(
          value <-- lenSignal,
          onInput.mapToValue --> { v =>
            onRegexLenChange(v.toIntOption)
          }
        )
      ),
      button(
        cls := "btn btn-sm btn-outline-danger",
        "−",
        onClick --> { _ => onRemove() }
      )
    )
  }
}
