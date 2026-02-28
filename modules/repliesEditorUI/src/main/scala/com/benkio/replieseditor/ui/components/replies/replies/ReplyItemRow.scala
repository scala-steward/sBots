package com.benkio.replieseditor.ui.components.replies.replies

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*
import com.raquo.laminar.codecs.StringAsIsCodec

object ReplyItemRow {

  def render(
      entryIdx: Int,
      replyIdx: Int,
      itemSignal: Signal[ReplyItem],
      allowedFiles: Signal[Vector[String]],
      onValueChange: String => Unit,
      onRemove: () => Unit
  ): Div = {
    val kindSignal  = itemSignal.map(_.kind).distinct
    val valueSignal = itemSignal.map(_.value)
    val datalistId  = s"replies-$entryIdx-$replyIdx"

    div(
      cls := "input-group input-group-sm mb-1",
      child <-- kindSignal.map {
        case ReplyItemKind.File =>
          input(
            cls                               := "form-control",
            typ                               := "text",
            htmlAttr("list", StringAsIsCodec) := datalistId,
            controlled(
              value <-- valueSignal,
              onInput.mapToValue --> onValueChange
            )
          )
        case ReplyItemKind.Text =>
          input(
            cls         := "form-control",
            typ         := "text",
            placeholder := "text reply",
            controlled(
              value <-- valueSignal,
              onInput.mapToValue --> onValueChange
            )
          )
      },
      child <-- kindSignal.map {
        case ReplyItemKind.File =>
          dataList(
            idAttr := datalistId,
            children <-- allowedFiles.combineWith(valueSignal).map { case (allowed, selected) =>
              val options =
                if allowed.contains(selected) then allowed
                else (Vector(selected) ++ allowed).distinct
              options.map(f => option(value := f))
            }
          )
        case ReplyItemKind.Text =>
          emptyNode
      },
      button(
        cls := "btn btn-outline-danger",
        "−",
        onClick --> { _ => onRemove() }
      )
    )
  }
}
