package com.benkio.botDB.media

import cats.data.NonEmptyList
import cats.effect.IO
import cats.effect.Resource
import cats.syntax.all.*
import com.benkio.botDB.media.MediaUpdater.MediaUpdaterImpl
import com.benkio.botDB.Logger.given
import com.benkio.botDB.TestData.*
import com.benkio.telegrambotinfrastructure.mocks.DBLayerMock
import com.benkio.telegrambotinfrastructure.mocks.RepositoryMock
import com.benkio.telegrambotinfrastructure.model.media.getMediaResourceFile
import com.benkio.telegrambotinfrastructure.model.media.MediaFileSource
import com.benkio.telegrambotinfrastructure.model.media.MediaResource
import com.benkio.telegrambotinfrastructure.model.media.MediaResource.MediaResourceFile
import com.benkio.telegrambotinfrastructure.model.MimeType
import com.benkio.telegrambotinfrastructure.model.SBotInfo.SBotId
import com.benkio.telegrambotinfrastructure.repository.db.DBMediaData
import munit.CatsEffectSuite
import org.http4s.syntax.literals.*
import org.http4s.Uri

import java.io.File
import java.nio.file.Path

class MediaUpdaterSpec extends CatsEffectSuite {

  val mediaEntities: List[DBMediaData] = List(google, amazon, facebook)
  val botId                            = SBotId("testbot")

  val repositoryMock = new RepositoryMock(
    getResourceByKindHandler = (location, inputBotId) =>
      IO.raiseUnless(inputBotId == botId)(
        Throwable(s"[MediaUpdaterSpec] getResourceByKindHandler called with unexpected botId: $inputBotId")
      ).as(
        NonEmptyList
          .one(
            NonEmptyList.fromListUnsafe(
              File(getClass.getResource(location).toURI).listFiles
                .map(f => MediaResourceFile(Resource.pure(f.toPath())): MediaResource[IO])
                .toList
            )
          )
      )
  )
  val dbLayerMock = DBLayerMock.mock(
    botId = botId,
    medias = mediaEntities
  )
  val mediaUpdater: MediaUpdaterImpl[IO] = MediaUpdaterImpl[IO](
    config = config,
    dbLayer = dbLayerMock,
    repository = repositoryMock
  )

  test("MediaUpdater.fetchRootBotFiles should return the expected root files") {
    assertIO(
      mediaUpdater.fetchRootBotFiles
        .flatMap(_.map(_.getMediaResourceFile).flatten.sequence)
        .use(_.pure[IO]),
      config.jsonLocation.flatMap(location =>
        File(getClass.getResource(location.value).toURI).listFiles.map(_.toPath())
      )
    )
  }

  test("MediaUpdater.filterMediaJsonFiles should return the expected json files") {
    assertIO(
      mediaUpdater.fetchRootBotFiles.flatMap(roots => mediaUpdater.filterMediaJsonFiles(roots)).use(_.pure[IO]),
      List(File(getClass.getResource("/testdata/test_list.json").toURI).toPath)
    )
  }

  test("MediaUpdater.parseMediaJsonFiles should parse valid json file") {
    val input: List[Path]               = List(File(getClass.getResource("/testdata/test_list.json").toURI).toPath())
    val expected: List[MediaFileSource] = List(
      MediaFileSource(
        filename = "test_testData.mp3",
        kinds = List(
          "kind1",
          "kind2"
        ),
        mime = MimeType.MPEG,
        sources = List(
          Right(
            value = uri"https://www.dropbox.com/scl/fi/hYPb0/test_testData.mp3?rlkey=BbLKm&dl=1"
          )
        )
      )
    )
    assertIO(
      mediaUpdater.parseMediaJsonFiles(input).use(_.pure[IO]),
      expected
    )
  }
}
