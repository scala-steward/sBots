package com.benkio.replieseditor.app

final class RepliesEditorFiltering(store: RepliesEditorStore, loader: RepliesEditorLoader) {

  def toggle(): Unit =
    store.filtersOpenVar.update(b => !b)

  def setFilterText(text: String): Unit = {
    store.filterTextVar.set(text)
    store.selectedBotVar.now().foreach { botId =>
      store.currentPageVar.set(1)
      loader.loadPage(botId, 1)
    }
  }
}

