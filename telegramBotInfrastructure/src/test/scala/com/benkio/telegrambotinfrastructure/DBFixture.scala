package com.benkio.telegrambotinfrastructure

import cats.effect.IO
import cats.effect.Resource
import com.benkio.telegrambotinfrastructure.botcapabilities.DBResourceAccess.DBResourceAccess
import com.benkio.telegrambotinfrastructure.botcapabilities.UrlFetcher
import doobie.Transactor
import log.effect.fs2.SyncLogWriter.consoleLogUpToLevel
import log.effect.LogLevels
import log.effect.LogWriter
import munit._
import org.http4s.ember.client._
import scalacache._

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import scala.io.BufferedSource
import scala.io.Source

trait DBFixture { self: FunSuite =>

  implicit val log: LogWriter[IO] = consoleLogUpToLevel(LogLevels.Info)

  val dbName: String                                          = "botDB.sqlite3"
  val resourcePath: String                                    = new File("./..").getCanonicalPath
  val dbPath: String                                          = s"$resourcePath/$dbName"
  val dbUrl: String                                           = s"jdbc:sqlite:$dbPath";
  val deleteDB: Boolean                                       = false
  val migrationPath: String                                   = resourcePath + "/botDB/src/main/resources/db/migrations"
  val emptyDBSingleCache: Cache[IO, String, (String, String)] = new EmptyCache[IO, (String, String)]
  val emptyDBListCache: Cache[IO, String, List[(String, String)]] = new EmptyCache[IO, List[(String, String)]]

  lazy val databaseFixture = FunFixture[(Connection, Resource[IO, DBResourceAccess[IO]], Transactor[IO])](
    setup = { _ =>
      Class.forName("org.sqlite.JDBC")
      val conn = DriverManager.getConnection(dbUrl)
      val ddls = getMigrations(migrationPath)
      ddls.foreach { ddl =>
        {
          val query     = ddl.getLines().mkString(" ")
          val statement = conn.createStatement()
          statement.executeUpdate(query)
        }
      }
      val transactor = Transactor.fromConnection[IO](conn)
      val resourceAccessResource: Resource[IO, DBResourceAccess[IO]] = for {
        httpClient <- EmberClientBuilder.default[IO].build
        urlFetcher <- UrlFetcher[IO](httpClient)
        dbResourceAccess = new DBResourceAccess[IO](
          transactor = transactor,
          urlFetcher = urlFetcher,
          log = log,
          dbCacheSingle = emptyDBSingleCache,
          dbCacheList = emptyDBListCache,
        )
      } yield dbResourceAccess

      conn.createStatement().executeUpdate("DELETE FROM timeout;")
      (conn, resourceAccessResource, transactor)
    },
    teardown = { conn_resourceAccess =>
      {
        if (deleteDB) Files.deleteIfExists(Paths.get(dbPath))
        else conn_resourceAccess._1.createStatement().executeUpdate("DELETE FROM timeout;")
        conn_resourceAccess._1.close()
        ()
      }
    }
  )

  def getMigrations(migrationPath: String): List[BufferedSource] = {
    val migrationDir = new File(migrationPath)
    val migrations = if (migrationDir.exists && migrationDir.isDirectory) {
      migrationDir.listFiles.filter(_.isFile).toList
    } else { List[File]() }
    migrations.map(m => Source.fromFile(m))
  }

}