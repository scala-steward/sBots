package com.benkio.replieseditor.ui.components.replies.files

import com.benkio.replieseditor.module.*
import com.raquo.laminar.api.L.*
import com.raquo.laminar.codecs.StringAsIsCodec

object FileRow {

  def render(
    entryIdx: Int,
    fileIdx: Int,
    fileSignal: Signal[String],
    allowedFilesVar: Var[Vector[String]],
    update: (EditableEntry => EditableEntry) => Unit
  ): Div = {
    val datalistId         = s"files-$entryIdx-$fileIdx"

    div(
      cls := "input-group input-group-sm mb-1",
      input(
        cls := "form-control",
        typ := "text",
        htmlAttr("list", StringAsIsCodec) := datalistId,
        controlled(
          value <-- fileSignal,
          onInput.mapToValue --> { v =>
            update(e0 => e0.copy(files = e0.files.updated(fileIdx, v)))
          }
        )
      ),
      dataList(
        idAttr := datalistId,
        children <-- allowedFilesVar.signal.combineWith(fileSignal).map { case (allowed, selected) =>
          val options =
            if (allowed.contains(selected)) allowed
            else (Vector(selected) ++ allowed).distinct
          options.map(f => option(value := f))
        }
      ),
      button(
        cls := "btn btn-outline-danger",
        "−",
        onClick --> { _ =>
          update(e0 => e0.copy(files = e0.files.patch(fileIdx, Nil, 1)))
        }
      )
    )
  }
}

