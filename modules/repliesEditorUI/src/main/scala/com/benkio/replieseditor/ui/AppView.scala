package com.benkio.replieseditor.ui

import com.benkio.replieseditor.app.RepliesEditorController
import com.benkio.replieseditor.ui.components.page.AppPage
import com.raquo.laminar.api.L.*

object AppView {

  def render: Div = {
    val controller = new RepliesEditorController()

    AppPage.render(controller)
  }
}

