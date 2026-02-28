package com.benkio.replieseditor.ui.components.page

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*

object TopBar {

  def render(
    bots: Signal[Vector[ApiBot]],
    selectedBotId: Signal[Option[String]],
    dirty: Signal[Boolean],
    onBotSelected: Option[String] => Unit,
    onReload: () => Unit,
    onToggleFilters: () => Unit,
    onAddNew: () => Unit,
    addDisabled: Signal[Boolean],
    onSave: () => Unit
  ): Div =
    div(
      cls := "d-flex align-items-end gap-2 mb-3",
      div(
        cls := "flex-grow-1",
        label(cls := "form-label", "Bot replies file"),
        select(
          cls := "form-select",
          value <-- selectedBotId.map(_.getOrElse("")),
          inContext { thisNode =>
            onChange.mapTo(thisNode.ref.value) --> { v =>
              onBotSelected(Option(v).filter(_.nonEmpty))
            }
          },
          option(value := "", "-- select a bot --"),
          children <-- bots.map(_.map(b => option(value := b.botId, s"${b.botName} (${b.botId})")))
        )
      ),
      button(
        cls := "btn btn-outline-secondary",
        "Reload",
        onClick --> { _ => onReload() }
      ),
      button(
        cls := "btn btn-outline-secondary",
        "Filters",
        disabled <-- selectedBotId.map(_.isEmpty),
        onClick --> { _ => onToggleFilters() }
      ),
      button(
        cls := "btn btn-outline-secondary",
        "+ reply",
        disabled <-- selectedBotId.map(_.isEmpty).combineWith(addDisabled).map { case (noBot, dis) => noBot || dis },
        onClick --> { _ => onAddNew() }
      ),
      button(
        cls := "btn btn-primary",
        child.text <-- dirty.map(d => if (d) "Save (unsaved)" else "Save"),
        disabled <-- selectedBotId.map(_.isEmpty),
        onClick --> { _ => onSave() }
      )
    )
}

