package com.benkio.replieseditor.ui.components.replies

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*

object RepliesGrid {

  def render(
      entries: Signal[Vector[EntryState]],
      allowedFiles: Signal[Vector[String]],
      onDelete: Int => Unit,
      onAddFileReply: Int => Unit,
      onAddTextReply: Int => Unit,
      onReplyValueChange: (Int, Int, String) => Unit,
      onRemoveReplyItem: (Int, Int) => Unit,
      onAddTrigger: Int => Unit,
      onRemoveTrigger: (Int, Int) => Unit,
      onTriggerKindChange: (Int, Int, TriggerKind) => Unit,
      onTriggerValueChange: (Int, Int, String) => Unit,
      onTriggerRegexLengthChange: (Int, Int, Option[Int]) => Unit
  ): Div =
    div(
      cls := "row g-3",
      children <-- entries.split(_.index) { (entryIndex, _, stSignal) =>
        ReplyCard.render(
          entryIndex = entryIndex,
          stSignal = stSignal,
          allowedFiles = allowedFiles,
          onDelete = onDelete,
          onAddFileReply = onAddFileReply,
          onAddTextReply = onAddTextReply,
          onReplyValueChange = onReplyValueChange,
          onRemoveReplyItem = onRemoveReplyItem,
          onAddTrigger = onAddTrigger,
          onRemoveTrigger = onRemoveTrigger,
          onTriggerKindChange = onTriggerKindChange,
          onTriggerValueChange = onTriggerValueChange,
          onTriggerRegexLengthChange = onTriggerRegexLengthChange
        )
      }
    )
}
