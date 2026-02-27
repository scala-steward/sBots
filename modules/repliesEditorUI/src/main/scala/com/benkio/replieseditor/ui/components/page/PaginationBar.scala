package com.benkio.replieseditor.ui.components.page

import com.raquo.laminar.api.L.*

object PaginationBar {

  private def totalPages(totalOpt: Option[Int], pageSize: Int): Int =
    totalOpt match {
      case None => 0
      case Some(total) =>
        val ps = pageSize.max(1)
        ((total + ps - 1) / ps).max(1)
    }

  def render(
    currentPageVar: Var[Int],
    totalOpt: Signal[Option[Int]],
    pageSizeVar: Var[Int],
    isLoading: Signal[Boolean],
    onPageRequested: Int => Unit
  ): Div = {
    val currentPageSignal = currentPageVar.signal.map(_.max(1))
    val pageSizeSignal    = pageSizeVar.signal.map(_.max(1))

    val totalPagesSignal =
      totalOpt.combineWith(pageSizeSignal).map { case (t, ps) => totalPages(t, ps) }

    val canPrevSignal = currentPageSignal.map(_ > 1)
    val canNextSignal = currentPageSignal.combineWith(totalPagesSignal).map { case (p, tp) => tp > 0 && p < tp }

    div(
      cls := "d-flex align-items-center justify-content-between gap-2 my-3",
      div(
        cls := "btn-group",
        button(
          cls := "btn btn-outline-secondary btn-sm",
          "Prev",
          disabled <-- canPrevSignal.not.combineWith(isLoading).map { case (noPrev, loading) => noPrev || loading },
          onClick --> { _ =>
            val p = currentPageVar.now().max(1)
            val next = (p - 1).max(1)
            currentPageVar.set(next)
            onPageRequested(next)
          }
        ),
        button(
          cls := "btn btn-outline-secondary btn-sm",
          "Next",
          disabled <-- canNextSignal.not.combineWith(isLoading).map { case (noNext, loading) => noNext || loading },
          onClick --> { _ =>
            val p = currentPageVar.now().max(1)
            val next = p + 1
            currentPageVar.set(next)
            onPageRequested(next)
          }
        )
      ),
      div(
        cls := "text-muted small",
        child.text <-- currentPageSignal.combineWith(totalPagesSignal).map { case (p, tp) =>
          if (tp == 0) "" else s"Page $p / $tp"
        }
      ),
      div(
        cls := "d-flex align-items-center gap-2",
        label(cls := "text-muted small mb-0", "Page size"),
        select(
          cls := "form-select form-select-sm",
          width := "6.5rem",
          controlled(
            value <-- pageSizeSignal.map(_.toString),
            onChange.mapToValue --> { v =>
              val parsed = v.toIntOption.getOrElse(pageSizeVar.now())
              val newPs  = parsed.max(1)
              pageSizeVar.set(newPs)
              currentPageVar.set(1)
              onPageRequested(1)
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
}

