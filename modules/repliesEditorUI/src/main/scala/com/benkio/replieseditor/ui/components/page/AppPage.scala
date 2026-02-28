package com.benkio.replieseditor.ui.components.page

import com.benkio.replieseditor.app.RepliesEditorController
import com.benkio.replieseditor.ui.components.replies.RepliesGrid
import com.raquo.laminar.api.L.*

object AppPage {

  def render(controller: RepliesEditorController): Div =
    div(
      onMountCallback(_ => controller.init()),
      TopBar.render(
        bots = controller.bots,
        selectedBotId = controller.selectedBotId,
        dirty = controller.dirty,
        onBotSelected = controller.selectBot,
        onReload = controller.reloadSelectedBot,
        onToggleFilters = controller.toggleFilters,
        onAddNew = controller.addNewReplyAtCurrentPageTop,
        addDisabled = controller.addDisabled,
        onSave = controller.commit
      ),
      FiltersPanel.render(
        isOpen = controller.filtersOpen,
        filterText = controller.filterText,
        isLoading = controller.isLoading,
        onFilterTextChange = controller.setFilterText
      ),
      StatusBar.render(controller.status),
      PaginationBar.render(
        pageSize = controller.pageSize,
        isLoading = controller.isLoading,
        canPrev = controller.canPrev,
        canNext = controller.canNext,
        label = controller.pageLabel,
        onPrev = controller.requestPrevPage,
        onNext = controller.requestNextPage,
        onPageSizeChange = controller.setPageSize
      ),
      RepliesGrid.render(
        entries = controller.entries,
        allowedFiles = controller.allowedFiles,
        onDelete = controller.deleteEntry,
        onAddFileReply = controller.addFileReplyItem,
        onAddTextReply = controller.addTextReplyItem,
        onReplyValueChange = controller.setReplyItemValue,
        onRemoveReplyItem = controller.removeReplyItem,
        onAddTrigger = controller.addTrigger,
        onRemoveTrigger = controller.removeTrigger,
        onTriggerKindChange = controller.setTriggerKind,
        onTriggerValueChange = controller.setTriggerValue,
        onTriggerRegexLengthChange = controller.setTriggerRegexLength
      )
    )
}
