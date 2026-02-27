package com.benkio.replieseditor.ui.components.page

import com.benkio.replieseditor.module.*
import com.benkio.replieseditor.ui.components.replies.RepliesGrid
import com.raquo.laminar.api.L.*

object AppPage {

  def render(
    bots: Signal[Vector[ApiBot]],
    selectedBotVar: Var[Option[String]],
    dirty: Signal[Boolean],
    status: Signal[Option[String]],
    entriesVar: Var[Vector[EntryState]],
    allowedFilesVar: Var[Vector[String]],
    paginationBar: HtmlElement,
    onMount: () => Unit,
    onBotSelected: Option[String] => Unit,
    onReload: () => Unit,
    onSave: () => Unit,
    onEditableChanged: (Int, EditableEntry) => Unit,
    markDirty: () => Unit
  ): Div =
    div(
      onMountCallback(_ => onMount()),
      TopBar.render(
        bots = bots,
        selectedBotVar = selectedBotVar,
        dirty = dirty,
        onBotSelected = onBotSelected,
        onReload = onReload,
        onSave = onSave
      ),
      StatusBar.render(status),
      paginationBar,
      RepliesGrid.render(
        entriesVar = entriesVar,
        allowedFilesVar = allowedFilesVar,
        markDirty = markDirty,
        onEditableChanged = onEditableChanged
      )
    )
}

