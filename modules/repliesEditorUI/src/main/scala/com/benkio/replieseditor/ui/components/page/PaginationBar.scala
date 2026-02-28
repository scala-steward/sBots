package com.benkio.replieseditor.ui.components.page

import com.raquo.laminar.api.L.*

object PaginationBar {
  def render(
      pageSize: Signal[Int],
      isLoading: Signal[Boolean],
      canPrev: Signal[Boolean],
      canNext: Signal[Boolean],
      label: Signal[String],
      onPrev: () => Unit,
      onNext: () => Unit,
      onPageSizeChange: Int => Unit
  ): Div =
    div(
      cls := "d-flex align-items-center justify-content-between gap-2 my-3",
      div(
        cls := "btn-group",
        button(
          cls := "btn btn-outline-secondary btn-sm",
          "Prev",
          disabled <-- canPrev.not.combineWith(isLoading).map { case (noPrev, loading) => noPrev || loading },
          onClick --> { _ => onPrev() }
        ),
        button(
          cls := "btn btn-outline-secondary btn-sm",
          "Next",
          disabled <-- canNext.not.combineWith(isLoading).map { case (noNext, loading) => noNext || loading },
          onClick --> { _ => onNext() }
        )
      ),
      div(
        cls := "text-muted small",
        child.text <-- label
      ),
      div(
        cls := "d-flex align-items-center gap-2",
        com.raquo.laminar.api.L.label(cls := "text-muted small mb-0", "Page size"),
        select(
          cls   := "form-select form-select-sm",
          width := "6.5rem",
          controlled(
            value <-- pageSize.map(_.max(1).toString),
            onChange.mapToValue --> { v =>
              val parsed = v.toIntOption.getOrElse(60)
              onPageSizeChange(parsed.max(1))
            }
          ),
          option(value := "25", "25"),
          option(value := "50", "50"),
          option(value := "60", "60"),
          option(value := "100", "100")
        )
      )
    )
}
