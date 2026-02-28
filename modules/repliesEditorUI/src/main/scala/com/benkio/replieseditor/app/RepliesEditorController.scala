package com.benkio.replieseditor.app

import com.benkio.replieseditor.module.TriggerKind

final class RepliesEditorController {
  private val store      = new RepliesEditorStore()
  private val loader     = new RepliesEditorLoader(store)
  private val pagination = new RepliesEditorPagination(store, loader)
  private val filtering  = new RepliesEditorFiltering(store, loader)
  private val mutations  = new RepliesEditorMutations(store, loader)

  // state / signals exposed to UI
  val bots          = store.bots
  val selectedBotId = store.selectedBotId
  val allowedFiles  = store.allowedFiles
  val entries       = store.entries
  val totalOpt      = store.totalOpt
  val currentPage   = store.currentPage
  val pageSize      = store.pageSize
  val isLoading     = store.isLoading
  val dirty         = store.dirty
  val status        = store.status
  val filtersOpen   = store.filtersOpen
  val filterText    = store.filterText
  val addDisabled   = store.addDisabled

  // pagination derived signals
  val canPrev   = pagination.canPrev
  val canNext   = pagination.canNext
  val pageLabel = pagination.pageLabel

  // lifecycle/actions used by UI
  def init(): Unit = loader.init()

  def selectBot(botIdOpt: Option[String]): Unit = {
    store.selectedBotVar.set(botIdOpt)
    botIdOpt.foreach(loader.loadBot)
  }

  def reloadSelectedBot(): Unit =
    store.selectedBotVar.now().foreach(loader.loadBot)

  def toggleFilters(): Unit             = filtering.toggle()
  def setFilterText(text: String): Unit = filtering.setFilterText(text)

  def requestPrevPage(): Unit    = pagination.requestPrev()
  def requestNextPage(): Unit    = pagination.requestNext()
  def setPageSize(ps: Int): Unit = pagination.setPageSize(ps)

  def addNewReplyAtCurrentPageTop(): Unit = mutations.addNewReplyAtCurrentPageTop()
  def deleteEntry(entryIndex: Int): Unit  = mutations.deleteEntry(entryIndex)
  def commit(): Unit                      = mutations.commit()

  def setReplyItemValue(entryIndex: Int, replyIdx: Int, value: String): Unit =
    mutations.setReplyItemValue(entryIndex, replyIdx, value)
  def addFileReplyItem(entryIndex: Int): Unit               = mutations.addFileReplyItem(entryIndex)
  def addTextReplyItem(entryIndex: Int): Unit               = mutations.addTextReplyItem(entryIndex)
  def removeReplyItem(entryIndex: Int, replyIdx: Int): Unit = mutations.removeReplyItem(entryIndex, replyIdx)

  def addTrigger(entryIndex: Int): Unit                     = mutations.addTrigger(entryIndex)
  def removeTrigger(entryIndex: Int, triggerIdx: Int): Unit = mutations.removeTrigger(entryIndex, triggerIdx)
  def setTriggerKind(entryIndex: Int, triggerIdx: Int, kind: TriggerKind): Unit =
    mutations.setTriggerKind(entryIndex, triggerIdx, kind)
  def setTriggerValue(entryIndex: Int, triggerIdx: Int, value: String): Unit =
    mutations.setTriggerValue(entryIndex, triggerIdx, value)
  def setTriggerRegexLength(entryIndex: Int, triggerIdx: Int, len: Option[Int]): Unit =
    mutations.setTriggerRegexLength(entryIndex, triggerIdx, len)
}
