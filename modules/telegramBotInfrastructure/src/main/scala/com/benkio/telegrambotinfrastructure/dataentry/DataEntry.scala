package com.benkio.telegrambotinfrastructure.dataentry

import cats.effect.IO
import cats.effect.Resource
import cats.syntax.all.*
import com.benkio.telegrambotinfrastructure.model.media.MediaFileSource
import io.circe.parser.*
import io.circe.syntax.*
import io.circe.Json

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import com.benkio.telegrambotinfrastructure.config.SBotConfig
import java.nio.file.Paths

object DataEntry {

  private[dataentry] def parseInput(links: List[String]): IO[List[MediaFileSource]] =
    links.traverse(link => MediaFileSource.fromUriString(link.replace("dl=0", "dl=1")))

  def dataEntryLogic(input: List[String], sBotConfig: SBotConfig) = {
    val jsonListFileResource =
      Resource.make(IO.delay(scala.io.Source.fromFile(sBotConfig.listJsonFilename)))(bufferedSorce =>
        IO.delay(bufferedSorce.close)
      )
    val listJsonFilepath = Paths.get(sBotConfig.listJsonFilename)
    val repliesJsonFileResource =
      Resource.make(IO.delay(scala.io.Source.fromFile(sBotConfig.repliesJsonFilename)))(bufferedSorce =>
        IO.delay(bufferedSorce.close)
      )
    val repliesJsonFilepath = Paths.get(sBotConfig.repliesJsonFilename)
    for {
      _ <- IO.println(
        s"[DataEntry:22:42]] Read the input ${input.length} links & parse them to Json"
      )
      mediafileSources <- parseInput(input)
      mediafileSourcesJson = mediafileSources.asJson
      _               <- IO.println("[DataEntry:24:45]] Read the json_list and parse it to json")
      botListFile     <- jsonListFileResource.use(_.mkString.pure[IO])
      botListFileJson <- IO.fromEither(parse(botListFile))
      _               <- IO.println("[DataEntry]] Merge the 2 arrays together list")
      mergedArrayList = {
        // Extract the arrays as lists of Json elements
        val elements1 = botListFileJson.asArray.getOrElse(Vector.empty)
        val elements2 = mediafileSourcesJson.asArray.getOrElse(Vector.empty)
        // Combine and convert back to Json
        Json.fromValues(elements1 ++ elements2)
      }
      _ <- IO.println("[DataEntry] Write the json back")
      _ <- IO(Files.write(listJsonFilepath, mergedArrayList.toString.getBytes(StandardCharsets.UTF_8)))

      _ <- IO.println("[DataEntry] create media file source groups")
      mediaFileSourceGroups = MediaFileSourceGroup.fromMediaFileSourceList(mediafileSources)
      _ <- IO.println("[DataEntry] convert media file source groups to ReplyBundleMessages")
      newReplyBundleMessages = mediaFileSourceGroups.map(MediaFileSourceGroup.toReplyBundleMessage).asJson
      _ <- IO.println("[DataEntry] Read the current replies")
      botRepliesFile     <- repliesJsonFileResource.use(_.mkString.pure[IO])
      botRepliesFileJson <- IO.fromEither(parse(botRepliesFile))
      _               <- IO.println("[DataEntry]] Merge the 2 arrays together replies")
      mergedArrayReplies = {
        // Extract the arrays as lists of Json elements
        val elements1 = newReplyBundleMessages.asArray.getOrElse(Vector.empty)
        val elements2 = botRepliesFileJson.asArray.getOrElse(Vector.empty)
        // Combine and convert back to Json
        Json.fromValues(elements1 ++ elements2)
      }
      _ <- IO.println("[DataEntry] Write the json back")
      _ <- IO(Files.write(repliesJsonFilepath, mergedArrayReplies.toString.getBytes(StandardCharsets.UTF_8)))
    } yield ()
  }
} // end DataEntry
