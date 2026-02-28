package com.benkio.replieseditor.app

import munit.FunSuite

class RepliesEditorFilteringSuite extends FunSuite {

  test("toggle flips filtersOpenVar") {
    val store     = new RepliesEditorStore()
    val filtering = new RepliesEditorFiltering(store, loader = null)
    assertEquals(store.filtersOpenVar.now(), false)
    filtering.toggle()
    assertEquals(store.filtersOpenVar.now(), true)
    filtering.toggle()
    assertEquals(store.filtersOpenVar.now(), false)
  }
}
