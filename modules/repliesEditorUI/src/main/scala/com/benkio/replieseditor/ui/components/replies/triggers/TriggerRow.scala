package com.benkio.replieseditor.ui.components.replies.triggers

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*

object TriggerRow {

  def render(
    triggerIdx: Int,
    triggerSignal: Signal[TriggerEdit],
    update: (EditableEntry => EditableEntry) => Unit
  ): Div = {
    val kindSignal  = triggerSignal.map(_.kind)
    val valueSignal = triggerSignal.map(_.value)
    val lenSignal   = triggerSignal.map(_.regexLength.map(_.toString).getOrElse(""))

    div(
      cls := "d-flex gap-1 align-items-center mb-1",
      select(
        cls := "form-select form-select-sm",
        value <-- kindSignal.map(k => if (k == TriggerKind.Regex) "regex" else "string"),
        inContext { thisNode =>
          onChange.mapTo(thisNode.ref.value) --> { v =>
            val k = if (v == "regex") TriggerKind.Regex else TriggerKind.PlainString
            update(e0 =>
              e0.copy(triggers =
                e0.triggers.updated(
                  triggerIdx,
                  e0.triggers(triggerIdx).copy(
                    kind = k,
                    regexLength =
                      if (k == TriggerKind.Regex)
                        e0.triggers(triggerIdx).regexLength.orElse(Some(e0.triggers(triggerIdx).value.length))
                      else None
                  )
                )
              )
            )
          }
        },
        option(value := "string", "String"),
        option(value := "regex", "Regex")
      ),
      input(
        cls := "form-control form-control-sm",
        placeholder := "trigger",
        controlled(
          value <-- valueSignal,
          onInput.mapToValue --> { v =>
            update(e0 =>
              e0.copy(triggers =
                e0.triggers.updated(triggerIdx, e0.triggers(triggerIdx).copy(value = v))
              )
            )
          }
        )
      ),
      input(
        cls := "form-control form-control-sm",
        width := "6rem",
        placeholder := "len",
        display <-- kindSignal.map(k => if (k == TriggerKind.Regex) "" else "none"),
        controlled(
          value <-- lenSignal,
          onInput.mapToValue --> { v =>
            val parsed = v.toIntOption
            update(e0 =>
              e0.copy(triggers =
                e0.triggers.updated(triggerIdx, e0.triggers(triggerIdx).copy(regexLength = parsed))
              )
            )
          }
        )
      ),
      button(
        cls := "btn btn-sm btn-outline-danger",
        "−",
        onClick --> { _ =>
          update(e0 => e0.copy(triggers = e0.triggers.patch(triggerIdx, Nil, 1)))
        }
      )
    )
  }
}

