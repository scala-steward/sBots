package com.benkio.telegrambotinfrastructure.model.media

import cats.effect.Resource
import telegramium.bots.IFile
import telegramium.bots.InputLinkFile
import telegramium.bots.InputPartFile

import java.nio.file.Path

enum MediaResource[F[_]] {
  case MediaResourceFile(file: Resource[F, Path]) extends MediaResource[F]
  case MediaResourceIFile(iFile: String)          extends MediaResource[F]
}

extension [F[_]](mediaResource: MediaResource[F]) {
  def toTelegramApi: Resource[F, IFile] = mediaResource match {
    case MediaResource.MediaResourceFile(rFile: Resource[F, Path]) => rFile.map(p => InputPartFile(p.toFile()))
    case MediaResource.MediaResourceIFile(iFile: String)           => Resource.pure(InputLinkFile(iFile))
  }
  def getMediaResourceFile: Option[Resource[F, Path]] = mediaResource match {
    case MediaResource.MediaResourceFile(file: Resource[F, Path]) => Some(file)
    case MediaResource.MediaResourceIFile(_)                      => None
  }
}
