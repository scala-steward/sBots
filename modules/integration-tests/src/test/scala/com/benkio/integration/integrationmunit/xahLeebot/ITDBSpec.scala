package com.benkio.integration.integrationmunit.xahleebot

import cats.effect.*
import cats.implicits.*
import com.benkio.integration.DBFixture
import com.benkio.telegrambotinfrastructure.BackgroundJobManager
import com.benkio.telegrambotinfrastructure.mocks.ApiMock.given
import com.benkio.telegrambotinfrastructure.mocks.DBLayerMock
import com.benkio.telegrambotinfrastructure.mocks.ResourceAccessMock
import com.benkio.telegrambotinfrastructure.model.reply.MediaFile
import com.benkio.telegrambotinfrastructure.model.media.MediaFileSource
import com.benkio.telegrambotinfrastructure.model.reply.ReplyBundle
import com.benkio.telegrambotinfrastructure.resources.db.DBLayer
import com.benkio.telegrambotinfrastructure.resources.db.DBMedia
import com.benkio.xahleebot.CommandRepliesData
import doobie.implicits.*
import io.circe.parser.decode
import java.io.File
import munit.CatsEffectSuite
import scala.io.Source

class ITDBSpec extends CatsEffectSuite with DBFixture {

  val botName: String           = "botname"
  val botPrefix: String         = "xah"
  val emptyDBLayer: DBLayer[IO] = DBLayerMock.mock(botName)
  val resourceAccessMock        = new ResourceAccessMock(List.empty)
  val emptyBackgroundJobManager: Resource[IO, BackgroundJobManager[IO]] = Resource.eval(
    BackgroundJobManager(
      dbSubscription = emptyDBLayer.dbSubscription,
      dbShow = emptyDBLayer.dbShow,
      resourceAccess = resourceAccessMock,
      botName = botName
    )
  )

  // File Reference Check

  databaseFixture.test(
    "commandRepliesData should never raise an exception when try to open the file in resounces"
  ) { fixture =>
    val transactor = fixture.transactor
    val resourceAssert = for {
      resourceDBLayer <- fixture.resourceDBLayer
      bjm             <- emptyBackgroundJobManager
      files <- Resource.eval(
        CommandRepliesData
          .values[IO](
            botName = botName,
            botPrefix = botPrefix,
            dbLayer = resourceDBLayer,
            backgroundJobManager = bjm,
          )
          .flatTraverse((r: ReplyBundle[IO]) => ReplyBundle.getMediaFiles[IO](r))
      )
      checks <- Resource.eval(
        files
          .traverse((file: MediaFile) =>
            DBMedia
              .getMediaQueryByName(file.filename)
              .unique
              .transact(transactor)
              .onError(_ => IO.println(s"[ERROR] file missing from the DB: " + file))
              .attempt
              .map(_.isRight)
          )
      )
    } yield checks.foldLeft(true)(_ && _)

    resourceAssert.use(IO.pure).assert
  }

  // File json file check

  databaseFixture.test(
    "commandRepliesData random files should be contained in the jsons"
  ) { fixture =>
    val listPath                                   = new File(s"./../bots/xahLeeBot").getCanonicalPath + "/xah_list.json"
    val jsonContent                                = Source.fromFile(listPath).getLines().mkString("\n")
    val json: Either[io.circe.Error, List[String]] = decode[List[MediaFileSource]](jsonContent).map(_.map(_.filename))

    val resourceAssert = for {
      resourceDBLayer <- fixture.resourceDBLayer
      bjm             <- emptyBackgroundJobManager
      mediaFiles <- Resource.eval(
        CommandRepliesData
          .values[IO](
            botName = botName,
            botPrefix = botPrefix,
            dbLayer = resourceDBLayer,
            backgroundJobManager = bjm,
          )
          .flatTraverse((r: ReplyBundle[IO]) => ReplyBundle.getMediaFiles[IO](r))
      )
      checks <- Resource.pure(
        mediaFiles
          .map((mediaFile: MediaFile) =>
            json.fold(
              e => fail("test failed", e),
              jsonMediaFileSources => {
                val result = jsonMediaFileSources.exists((mediaFilenameSource: String) =>
                  mediaFilenameSource == mediaFile.filename
                )
                if (!result) {
                  println(s"${mediaFile.filename} is not contained in the json file")
                }
                result
              }
            )
          )
      )
    } yield checks.foldLeft(true)(_ && _)

    resourceAssert.use(IO.pure).assert
  }
}
