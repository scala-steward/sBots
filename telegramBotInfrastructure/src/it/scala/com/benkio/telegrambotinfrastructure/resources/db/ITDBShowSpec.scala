package com.benkio.telegrambotinfrastructure.resources.db

import com.benkio.telegrambotinfrastructure.model.ShowQuery
import com.benkio.telegrambotinfrastructure.model.RandomQuery
import com.benkio.telegrambotinfrastructure.resources.db.DBShowData
import doobie.Transactor
import java.sql.DriverManager
import cats.effect.Resource
import com.benkio.telegrambotinfrastructure.DBFixture
import munit.CatsEffectSuite
import doobie.munit.analysisspec.IOChecker

import cats.effect.IO

class ITDBShowSpec extends CatsEffectSuite with DBFixture with IOChecker {

  val botName = "RichardPHJBensonBot"

  val testShow: DBShowData = DBShowData(
    show_url = "https://www.youtube.com/watch?v=8lNe8xmtgc8",
    bot_name = botName,
    show_title = "Cocktail Micidiale 21 gennaio 2005 (puntata completa) il segreto di Brian May, Paul Gilbert",
    show_upload_date = "20180610",
    show_duration = 1367,
    show_description = Some("""#RichardBenson #CocktailMicidiale #PaulGilbert
arriva il peggio del peggio""")
  )

  override def transactor: doobie.Transactor[cats.effect.IO] = {
    Class.forName("org.sqlite.JDBC")
    val conn = DriverManager.getConnection(dbUrl)
    runMigrations(dbUrl, migrationTable, migrationPath)
    val transactor = Transactor.fromConnection[IO](conn)
    transactor
  }

  databaseFixture.test(
    "DBShow queries should check"
  ) { fixture =>
    fixture.resourceDBLayer
      .map(_.dbShow)
      .use(dbShow =>
        for {
          _ <- IO(checkOutput(dbShow.getShowsQuery("botName")))
          _ <- IO(check(dbShow.getShowByShowQueryQuery(RandomQuery, "botName")))
          _ <- IO(
            check(
              dbShow
                .getShowByShowQueryQuery(ShowQuery("title=paul+gilbert&description=samurai&minDuration=300"), "botName")
            )
          )
        } yield ()
      )
      .assert
  }

  databaseFixture.test(
    "DBShow: should return the test show sample"
  ) { fixture =>
    val resourceAssert = for {
      dbShow      <- fixture.resourceDBLayer.map(_.dbShow)
      bensonShows <- Resource.eval(dbShow.getShows(botName))
      bensonShowsByKeyword <- Resource.eval(
        dbShow.getShowByShowQuery(ShowQuery("title=il+segreto+di+Brian+May,+Paul+Gilbert"), botName)
      )
    } yield (bensonShows, bensonShowsByKeyword)

    resourceAssert.use { case (bensonShows, bensonShowsByKeyword) =>
      IO {
        assert(bensonShows.exists(_ == testShow))
        assert(bensonShowsByKeyword.exists(_ == testShow))
      }
    }
  }
}