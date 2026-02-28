package com.benkio.replieseditor.app

import munit.FunSuite

class RepliesEditorLogicSuite extends FunSuite {

  test("totalPages returns 0 when total is None") {
    assertEquals(RepliesEditorLogic.totalPages(None, pageSize = 60), 0)
  }

  test("totalPages rounds up") {
    assertEquals(RepliesEditorLogic.totalPages(Some(1), pageSize = 60), 1)
    assertEquals(RepliesEditorLogic.totalPages(Some(60), pageSize = 60), 1)
    assertEquals(RepliesEditorLogic.totalPages(Some(61), pageSize = 60), 2)
  }

  test("pageLabel is empty when totalPages is 0") {
    assertEquals(RepliesEditorLogic.pageLabel(1, None, 60), "")
  }

  test("pageLabel formats correctly") {
    assertEquals(RepliesEditorLogic.pageLabel(1, Some(61), 60), "Page 1 / 2")
    assertEquals(RepliesEditorLogic.pageLabel(2, Some(61), 60), "Page 2 / 2")
  }

  test("insertOffsetAtPageTop clamps to total") {
    assertEquals(RepliesEditorLogic.insertOffsetAtPageTop(total = 0, currentPage = 1, pageSize = 60), 0)
    assertEquals(RepliesEditorLogic.insertOffsetAtPageTop(total = 10, currentPage = 1, pageSize = 60), 0)
    assertEquals(RepliesEditorLogic.insertOffsetAtPageTop(total = 10, currentPage = 2, pageSize = 60), 10)
    assertEquals(RepliesEditorLogic.insertOffsetAtPageTop(total = 120, currentPage = 2, pageSize = 60), 60)
  }
}
