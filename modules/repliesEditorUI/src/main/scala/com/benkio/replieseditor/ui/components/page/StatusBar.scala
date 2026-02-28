package com.benkio.replieseditor.ui.components.page

import com.raquo.laminar.api.L.*

object StatusBar {
  def render(status: Signal[Option[String]]): Div =
    div(
      child.maybe <-- status.map(_.map(msg => div(cls := "alert alert-info py-2", msg)))
    )
}
