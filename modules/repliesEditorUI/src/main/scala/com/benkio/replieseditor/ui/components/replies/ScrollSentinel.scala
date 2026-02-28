package com.benkio.replieseditor.ui.components.replies

import com.raquo.laminar.api.L.*
import org.scalajs.dom

object ScrollSentinel {

  def render(onVisible: () => Unit, isLoading: Signal[Boolean]): Div = {
    {
      var observer: Option[dom.IntersectionObserver] = None

      div(
        cls := "py-3 text-center text-muted small",
        child.text <-- isLoading.map(l => if l then "Loading more…" else ""),
        onMountCallback { ctx =>
          val node = ctx.thisNode.ref
          val obs  = new dom.IntersectionObserver(
            (entries, _) => {
              val anyVisible = entries.exists(_.isIntersecting)
              if anyVisible then onVisible()
            },
            new dom.IntersectionObserverInit {
              root = null
              rootMargin = "300px"
              threshold = 0.01
            }
          )
          obs.observe(node)
          observer = Some(obs)
        },
        onUnmountCallback { _ =>
          observer.foreach(_.disconnect())
          observer = None
        }
      )
    }
  }
}
