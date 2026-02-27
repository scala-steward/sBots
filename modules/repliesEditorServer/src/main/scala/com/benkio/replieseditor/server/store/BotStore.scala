package com.benkio.replieseditor.server.store

import cats.effect.IO
import cats.effect.Ref
import cats.syntax.all.*
import com.benkio.replieseditor.server.jsonio.{ListJsonFile, RepliesJsonFile, TriggersTxtFile}
import com.benkio.replieseditor.server.module.{ApiBot, ApiError, BotFiles, SaveOk}
import com.benkio.replieseditor.server.validation.MediaFilesAllowedValidation
import com.benkio.telegrambotinfrastructure.model.reply.ReplyBundleMessage
import io.circe.Json
import io.circe.syntax.*

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.*

final class BotStore private (ref: Ref[IO, BotStore.State]) {

  def listBots: IO[Vector[ApiBot]] =
    ref.get.map(_.bots.map(b => ApiBot(b.files.botId, b.files.botName)))

  def getReplies(botId: String): IO[Either[ApiError, Json]] =
    ref.get.map(_.byId.get(botId) match {
      case None => Left(ApiError(s"Unknown botId: $botId"))
      case Some(b) =>
        b.replies match {
          case Left(err)   => Left(ApiError(s"Failed to load replies for $botId: $err"))
          case Right(json) => Right(json)
        }
    })

  def getAllowedFiles(botId: String): IO[Either[ApiError, Vector[String]]] =
    ref.get.map(_.byId.get(botId) match {
      case None => Left(ApiError(s"Unknown botId: $botId"))
      case Some(b) =>
        b.allowedFiles match {
          case Left(err)    => Left(ApiError(s"Failed to load allowed files for $botId: $err"))
          case Right(files) => Right(files)
        }
    })

  def saveReplies(
    botId: String,
    replies: List[ReplyBundleMessage]
  ): IO[Either[ApiError, SaveOk]] =
    ref.get.flatMap { st =>
      st.byId.get(botId) match {
        case None => IO.pure(Left(ApiError(s"Unknown botId: $botId")))
        case Some(bot) =>
          bot.allowedFiles match {
            case Left(err) =>
              IO.pure(Left(ApiError(s"Cannot validate files, allowed files not loaded: $err")))
            case Right(allowedVec) =>
              val allowed = allowedVec.toSet
              MediaFilesAllowedValidation.validateAllFilesAreAllowed(replies, allowed) match {
                case Left(err) => IO.pure(Left(err))
                case Right(_) =>
                  val json = replies.asJson
                  for {
                    _ <- RepliesJsonFile.writePretty(bot.files.repliesJson, json.spaces2)
                    _ <- TriggersTxtFile.write(bot.files.triggersTxt, replies)
                    _ <- ref.update(_.updateReplies(botId, Right(json)))
                  } yield Right(SaveOk(botId = botId, repliesCount = replies.length))
              }
          }
      }
    }
}

object BotStore {

  final case class CachedBot(
    files: BotFiles,
    replies: Either[String, Json],
    allowedFiles: Either[String, Vector[String]]
  )

  final case class State(bots: Vector[CachedBot], byId: Map[String, CachedBot]) {
    def updateReplies(botId: String, replies: Either[String, Json]): State =
      byId.get(botId) match {
        case None => this
        case Some(b) =>
          val updated = b.copy(replies = replies)
          copy(
            bots = bots.map(x => if (x.files.botId == botId) updated else x),
            byId = byId.updated(botId, updated)
          )
      }
  }

  object State {
    val empty: State = State(Vector.empty, Map.empty)
  }

  def build(repoRoot: Path): IO[BotStore] =
    for {
      bots <- scanBots(repoRoot)
      cached <- bots.traverse(loadCached)
      st = State(cached.toVector, cached.map(b => b.files.botId -> b).toMap)
      ref <- Ref.of[IO, State](st)
    } yield new BotStore(ref)

  private def scanBots(repoRoot: Path): IO[List[BotFiles]] =
    IO.blocking {
      val botsRoot = repoRoot.resolve("modules").resolve("bots")
      if !Files.isDirectory(botsRoot) then List.empty
      else {
        Files
          .list(botsRoot)
          .iterator()
          .asScala
          .toList
          .filter(Files.isDirectory(_))
          .flatMap { botDir =>
            val botName = botDir.getFileName.toString
            val listJsonOpt =
              Files
                .newDirectoryStream(botDir, "*_list.json")
                .iterator()
                .asScala
                .toList
                .sortBy(_.getFileName.toString)
                .headOption

            listJsonOpt.flatMap { listJson =>
              val botId      = listJson.getFileName.toString.stripSuffix("_list.json")
              val repliesJson =
                botDir.resolve("src").resolve("main").resolve("resources").resolve(s"${botId}_replies.json")
              val triggersTxt = botDir.resolve(s"${botId}_triggers.txt")

              if Files.isRegularFile(repliesJson) && Files.isRegularFile(listJson)
              then Some(BotFiles(botId, botName, repliesJson, listJson, triggersTxt))
              else None
            }
          }
          .sortBy(_.botId)
      }
    }

  private def loadCached(files: BotFiles): IO[CachedBot] = {
    val repliesIO =
      RepliesJsonFile.read(files.repliesJson).attempt.map {
        case Left(ex)   => Left(ex.getMessage)
        case Right(json) => Right(json)
      }

    val allowedIO =
      ListJsonFile.readFilenamesSorted(files.listJson).attempt.map {
        case Left(ex)    => Left(ex.getMessage)
        case Right(list) => Right(list.toVector)
      }

    (repliesIO, allowedIO).mapN { (replies, allowed) =>
      CachedBot(files = files, replies = replies, allowedFiles = allowed)
    }
  }
}

