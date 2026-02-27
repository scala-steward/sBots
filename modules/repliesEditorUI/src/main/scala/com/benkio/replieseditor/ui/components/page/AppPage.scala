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
    filtersOpenVar: Var[Boolean],
    filterTextVar: Var[String],
    isLoading: Signal[Boolean],
    addDisabled: Signal[Boolean],
    onMount: () => Unit,
    onBotSelected: Option[String] => Unit,
    onReload: () => Unit,
    onAddNew: () => Unit,
    onSave: () => Unit,
    onEditableChanged: (Int, EditableEntry) => Unit,
    onDelete: Int => Unit,
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
        filtersOpenVar = filtersOpenVar,
        onAddNew = onAddNew,
        addDisabled = addDisabled,
        onSave = onSave
      ),
      FiltersPanel.render(isOpen = filtersOpenVar.signal, filterTextVar = filterTextVar, isLoading = isLoading),
      StatusBar.render(status),
      paginationBar,
      RepliesGrid.render(
        entriesVar = entriesVar,
        allowedFilesVar = allowedFilesVar,
        markDirty = markDirty,
        onEditableChanged = onEditableChanged,
        onDelete = onDelete
      )
    )
}

