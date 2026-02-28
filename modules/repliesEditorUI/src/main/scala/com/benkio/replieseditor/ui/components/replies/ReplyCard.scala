package com.benkio.replieseditor.ui.components.replies

import com.benkio.replieseditor.module.*
import com.benkio.replieseditor.ui.components.replies.replies.RepliesEditor
import com.benkio.replieseditor.ui.components.replies.triggers.TriggersEditor
import com.raquo.laminar.api.L.*

object ReplyCard {

  def render(
    entryIndex: Int,
    stSignal: Signal[EntryState],
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
      cls := "col-12 col-md-4",
      div(
        cls := "card shadow-sm",
        div(
          cls := "card-body",
          div(
            cls := "d-flex align-items-start justify-content-between gap-2",
            h6(cls := "card-title mb-2", s"Reply #${entryIndex + 1}"),
            div(
              cls := "btn-group btn-group-sm",
              button(
                cls := "btn btn-outline-danger",
                "Delete",
                title := "Delete this reply",
                onClick --> { _ => onDelete(entryIndex) }
              )
            )
          ),
          child <-- stSignal.map(_.editable.isDefined).distinct.map {
            case false =>
              div(
                cls := "text-muted small",
                "Non-editable entry (kept as-is on save)."
              )
            case true =>
              val editableSignal = stSignal.map(_.editable).map(_.get)
              div(
                RepliesEditor.render(
                  entryIndex = entryIndex,
                  replies = editableSignal.map(_.replies),
                  allowedFiles = allowedFiles,
                  onAddFile = () => onAddFileReply(entryIndex),
                  onAddText = () => onAddTextReply(entryIndex),
                  onValueChange = (replyIdx, v) => onReplyValueChange(entryIndex, replyIdx, v),
                  onRemove = replyIdx => onRemoveReplyItem(entryIndex, replyIdx)
                ),
                TriggersEditor.render(
                  triggers = editableSignal.map(_.triggers),
                  onAddTrigger = () => onAddTrigger(entryIndex),
                  onRemoveTrigger = ti => onRemoveTrigger(entryIndex, ti),
                  onKindChange = (ti, k) => onTriggerKindChange(entryIndex, ti, k),
                  onValueChange = (ti, v) => onTriggerValueChange(entryIndex, ti, v),
                  onRegexLenChange = (ti, v) => onTriggerRegexLengthChange(entryIndex, ti, v)
                )
              )
          }
        )
      )
    )
}

