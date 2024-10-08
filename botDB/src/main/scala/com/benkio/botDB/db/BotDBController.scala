package com.benkio.botDB.db

import com.benkio.telegrambotinfrastructure.model.MediaFileSource
import cats.effect.Resource
import cats.effect.Sync
import cats.implicits.*
import com.benkio.botDB.db.schema.MediaEntity
import com.benkio.botDB.Config
import com.benkio.telegrambotinfrastructure.resources.ResourceAccess
import io.circe.parser.decode

import java.sql.Timestamp
import java.time.Instant
import scala.io.Source

sealed trait BotDBController[F[_]] {
  def build: Resource[F, Unit]

  def populateMediaTable: Resource[F, Unit]
}

object BotDBController {
  def apply[F[_]: Sync](
      cfg: Config,
      databaseRepository: DatabaseRepository[F],
      resourceAccess: ResourceAccess[F],
      migrator: DBMigrator[F]
  ): BotDBController[F] =
    new BotDBControllerImpl(
      cfg = cfg,
      databaseRepository = databaseRepository,
      resourceAccess = resourceAccess,
      migrator = migrator
    )

  private class BotDBControllerImpl[F[_]: Sync](
      cfg: Config,
      databaseRepository: DatabaseRepository[F],
      resourceAccess: ResourceAccess[F],
      migrator: DBMigrator[F]
  ) extends BotDBController[F] {
    override def build: Resource[F, Unit] = for {
      _ <- Resource.eval(migrator.migrate(cfg))
      _ <- populateMediaTable
    } yield ()

    override def populateMediaTable: Resource[F, Unit] = for {
      allFiles <- cfg.jsonLocation.flatTraverse(resourceAccess.getResourcesByKind)
      jsons = allFiles.filter(f => f.getName.endsWith("json"))
      input <- Resource.eval(Sync[F].fromEither(jsons.flatTraverse(json => {
        val fileContent = Source.fromFile(json).getLines().mkString("\n")
        decode[List[MediaFileSource]](fileContent)
      })))
      _ <- Resource.eval(
        input.traverse_(i =>
          for {
            mime <- MediaEntity.mimeTypeOrDefault[F](i.filename, i.mime)
            _ <- databaseRepository
              .insertMedia(
                MediaEntity(
                  media_name = i.filename,
                  kinds = i.kinds.getOrElse(List.empty),
                  mime_type = mime,
                  media_uri = i.uri,
                  created_at = Timestamp.from(Instant.now())
                )
              )
            _ <- Sync[F].delay(println(s"Inserted file ${i.filename} of kinds ${i.kinds} from ${i.uri}, successfully"))
          } yield ()
        )
      )
    } yield ()
  }
}
