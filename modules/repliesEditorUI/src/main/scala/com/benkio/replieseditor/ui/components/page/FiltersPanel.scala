package com.benkio.replieseditor.ui.components.page

import com.raquo.laminar.api.L.*

object FiltersPanel {

  def render(
    isOpen: Signal[Boolean],
    filterTextVar: Var[String],
    isLoading: Signal[Boolean]
  ): Div =
    div(
      child <-- isOpen.map {
        case false => emptyNode
        case true =>
          div(
            cls := "mb-3",
            label(cls := "form-label", "Filter by message"),
            textArea(
              cls := "form-control",
              rows := 3,
              placeholder := "Type a message to show only matching replies… (empty = no filter)",
              controlled(
                value <-- filterTextVar.signal,
                onInput.mapToValue --> filterTextVar
              )
            ),
            div(
              cls := "form-text",
              child.text <-- isLoading.map(l => if (l) "Filtering…" else "Filtering uses bot matching logic.")
            )
          )
      }
    )
}

