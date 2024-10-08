package com.benkio.botDB.db

import cats.effect.kernel.MonadCancelThrow
import cats.implicits.*
import com.benkio.botDB.db.schema.MediaEntity
import doobie.*
import doobie.implicits.*
import io.circe.syntax.*

trait DatabaseRepository[F[_]] {
  def insertMedia(mediaEntity: MediaEntity): F[Unit]
}

object DatabaseRepository {
  def apply[F[_]: MonadCancelThrow](transactor: Transactor[F]): DatabaseRepository[F] =
    new DatabaseRepositoryImpl[F](transactor = transactor)

  private class DatabaseRepositoryImpl[F[_]: MonadCancelThrow](transactor: Transactor[F])
      extends DatabaseRepository[F] {
    override def insertMedia(mediaEntity: MediaEntity): F[Unit] =
      insertSql(mediaEntity).run.transact(transactor).void.exceptSql {
        case e if e.getMessage().contains("UNIQUE constraint failed") =>
          updateOnConflictSql(mediaEntity).run.transact(transactor).void
        case e =>
          MonadCancelThrow[F].raiseError(
            new RuntimeException(s"An error occurred in inserting $mediaEntity with exception: $e")
          )
      }

    private def insertSql(mediaEntity: MediaEntity): Update0 =
      sql"INSERT INTO media (media_name, kinds, mime_type, media_url, created_at, media_count) VALUES (${mediaEntity.media_name}, ${mediaEntity.kinds.asJson.noSpaces}, ${mediaEntity.mime_type}, ${mediaEntity.media_uri.toString}, ${mediaEntity.created_at}, 0);".update

    private def updateOnConflictSql(mediaEntity: MediaEntity): Update0 =
      sql"UPDATE media SET kinds = ${mediaEntity.kinds.asJson.noSpaces}, media_url = ${mediaEntity.media_uri.toString} WHERE media_name = ${mediaEntity.media_name};".update
  }
}
