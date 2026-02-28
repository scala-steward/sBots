package com.benkio.replieseditor.app

import com.benkio.replieseditor.module.*
import io.circe.parser.parse
import munit.FunSuite

class RepliesEditorStoreSuite extends FunSuite {

  test("setStatus / clearStatus update statusVar") {
    val store = new RepliesEditorStore()
    store.setStatus("hi")
    assertEquals(store.statusVar.now(), Some("hi"))
    store.clearStatus()
    assertEquals(store.statusVar.now(), None)
  }

  test("markDirty updates dirtyVar") {
    val store = new RepliesEditorStore()
    assertEquals(store.dirtyVar.now(), false)
    store.markDirty()
    assertEquals(store.dirtyVar.now(), true)
  }

  test("setChunk fills entriesVar and totalVar") {
    val store = new RepliesEditorStore()
    val json1 = parse(
      """{ "trigger": { "TextTrigger": { "triggers": [ { "StringTextTriggerValue": "a" } ] } }, "reply": { "TextReply": { "text": ["x"], "replyToMessage": false } }, "matcher": "ContainsOnce" }"""
    ).toOption.get
    val json2 = parse(
      """{ "trigger": { "TextTrigger": { "triggers": [ { "StringTextTriggerValue": "b" } ] } }, "reply": { "TextReply": { "text": ["y"], "replyToMessage": false } }, "matcher": "ContainsOnce" }"""
    ).toOption.get

    val chunk =
      RepliesChunk(
        total = 2,
        offset = 0,
        items = Vector(
          IndexedReply(index = 10, value = json1),
          IndexedReply(index = 11, value = json2)
        )
      )

    store.setChunk(chunk)
    assertEquals(store.totalVar.now(), Some(2))
    assertEquals(store.entriesVar.now().map(_.index), Vector(10, 11))
    assertEquals(store.entriesVar.now().flatMap(_.editable.map(_.matcher)), Vector("ContainsOnce", "ContainsOnce"))
  }
}
