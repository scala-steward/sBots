package com.benkio.replieseditor.ui.components.replies.replies

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*

object RepliesEditor {

  def render(
    entryIdx: Int,
    editableVar: Var[EditableEntry],
    allowedFilesVar: Var[Vector[String]],
    update: (EditableEntry => EditableEntry) => Unit
  ): Div =
    div(
      div(cls := "fw-semibold mb-1", "Replies"),
      div(
        children <-- editableVar.signal
          .map(_.replies)
          .splitByIndex { (replyIdx, _, itemSignal) =>
            ReplyItemRow.render(
              entryIdx = entryIdx,
              replyIdx = replyIdx,
              itemSignal = itemSignal,
              allowedFilesVar = allowedFilesVar,
              update = update
            )
          }
      ),
      div(
        cls := "btn-group mb-3",
        button(
          cls := "btn btn-sm btn-outline-secondary",
          "+ file",
          onClick --> { _ =>
            val first = allowedFilesVar.now().headOption.getOrElse("")
            update { e0 =>
              val hasText = e0.replies.exists(_.kind == ReplyItemKind.Text)
              val next    = ReplyItem(ReplyItemKind.File, first)
              if (hasText) e0.copy(replies = Vector(next))
              else e0.copy(replies = e0.replies :+ next)
            }
          }
        ),
        button(
          cls := "btn btn-sm btn-outline-secondary",
          "+ text",
          onClick --> { _ =>
            update { e0 =>
              val hasFile = e0.replies.exists(_.kind == ReplyItemKind.File)
              val next    = ReplyItem(ReplyItemKind.Text, "")
              if (hasFile) e0.copy(replies = Vector(next))
              else e0.copy(replies = e0.replies :+ next)
            }
          }
        )
      )
    )
}

