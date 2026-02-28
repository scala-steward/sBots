package com.benkio.replieseditor.ui.components.replies.replies

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*

object RepliesEditor {

  def render(
      entryIndex: Int,
      replies: Signal[Vector[ReplyItem]],
      allowedFiles: Signal[Vector[String]],
      onAddFile: () => Unit,
      onAddText: () => Unit,
      onValueChange: (Int, String) => Unit,
      onRemove: Int => Unit
  ): Div =
    div(
      div(cls := "fw-semibold mb-1", "Replies"),
      div(
        children <-- replies.splitByIndex { (replyIdx, _, itemSignal) =>
          ReplyItemRow.render(
            entryIdx = entryIndex,
            replyIdx = replyIdx,
            itemSignal = itemSignal,
            allowedFiles = allowedFiles,
            onValueChange = v => onValueChange(replyIdx, v),
            onRemove = () => onRemove(replyIdx)
          )
        }
      ),
      div(
        cls := "btn-group mb-3",
        button(
          cls := "btn btn-sm btn-outline-secondary",
          "+ file",
          onClick --> { _ => onAddFile() }
        ),
        button(
          cls := "btn btn-sm btn-outline-secondary",
          "+ text",
          onClick --> { _ => onAddText() }
        )
      )
    )
}
