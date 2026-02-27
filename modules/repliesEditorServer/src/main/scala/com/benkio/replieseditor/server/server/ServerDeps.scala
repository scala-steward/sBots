package com.benkio.replieseditor.server.server

import cats.effect.IO
import com.benkio.replieseditor.server.load.RepoRoot
import com.benkio.replieseditor.server.store.BotStore

import java.nio.file.Path

final case class ServerDeps(
    repoRoot: Path,
    botStore: BotStore
)

object ServerDeps {
  def build(): IO[ServerDeps] = {
    val root = RepoRoot.detect()
    BotStore.build(root).map(store => ServerDeps(repoRoot = root, botStore = store))
  }
}

