package com.benkio.telegrambotinfrastructure.patterns

import cats.Applicative
import cats.effect.Async
import cats.effect.Resource
import cats.implicits._
import com.benkio.telegrambotinfrastructure.botcapabilities.ResourceAccess
import com.benkio.telegrambotinfrastructure.messagefiltering.MessageMatches
import com.benkio.telegrambotinfrastructure.model.CommandTrigger
import com.benkio.telegrambotinfrastructure.model.ReplyBundleCommand
import com.benkio.telegrambotinfrastructure.model.ReplyBundleMessage
import com.benkio.telegrambotinfrastructure.model.TextReply
import com.benkio.telegrambotinfrastructure.model.TextTrigger
import com.benkio.telegrambotinfrastructure.model.TextTriggerValue
import log.effect.LogWriter
import org.http4s.Uri
import telegramium.bots.Message

import java.nio.file.Files
import scala.io.Source
import scala.util.Random

object CommandPatterns {

  object RandomLinkCommand {

    lazy val random = new Random()

    def selectRandomLinkReplyBundleCommand[F[_]: Async](
        resourceAccess: ResourceAccess[F],
        youtubeLinkSources: String
    )(implicit log: LogWriter[F]): F[ReplyBundleCommand[F]] =
      ReplyBundleCommand(
        trigger = CommandTrigger("randomshow"),
        text = Some(
          TextReply[F](
            _ =>
              selectRandomLinkByKeyword[F](
                "",
                resourceAccess,
                youtubeLinkSources
              )
                .use(optMessage => Applicative[F].pure(optMessage.toList)),
            true
          )
        ),
      ).pure[F]

    def selectRandomLinkByKeywordsReplyBundleCommand[F[_]: Async](
        resourceAccess: ResourceAccess[F],
        botName: String,
        youtubeLinkSources: String
    )(implicit log: LogWriter[F]): F[ReplyBundleCommand[F]] =
      ReplyBundleCommand[F](
        trigger = CommandTrigger("randomshowkeyword"),
        text = Some(
          TextReply[F](
            m =>
              handleCommandWithInput[F](
                m,
                "randomshowkeyword",
                botName,
                keywords =>
                  RandomLinkCommand
                    .selectRandomLinkByKeyword[F](
                      keywords,
                      resourceAccess,
                      youtubeLinkSources
                    )
                    .use(_.foldl(List(s"Nessuna puntata/show contenente '$keywords' è stata trovata")) { case (_, v) =>
                      List(v)
                    }.pure[F]),
                s"Inserisci una keyword da cercare tra le puntate/shows"
              ),
            true
          )
        ),
      ).pure[F]

    private def selectRandomLinkByKeyword[F[_]: Async](
        keywords: String,
        resourceAccess: ResourceAccess[F],
        youtubeLinkSources: String
    )(implicit log: LogWriter[F]): Resource[F, Option[String]] = for {
      _           <- Resource.eval(log.info(s"selectRandomLinkByKeyword for $keywords - $youtubeLinkSources"))
      sourceFiles <- resourceAccess.getResourcesByKind(youtubeLinkSources)
      sourceRawBytesArray = sourceFiles.map(f => Files.readAllBytes(f.toPath))
      sourceRawBytes = sourceRawBytesArray.foldLeft(Array.empty[Byte]) { case (acc, bs) =>
        acc ++ (('\n'.toByte) +: bs)
      }
      youtubeLinkReplies = Source
        .fromBytes(sourceRawBytes)
        .getLines()
        .toList
        .filter(s =>
          keywords
            .split(' ')
            .map(_.toLowerCase)
            .forall(k => s.toLowerCase.contains(k))
        )
      lineSelectedIndex <-
        if (!youtubeLinkReplies.isEmpty)
          Resource.eval(Async[F].delay(random.between(0, youtubeLinkReplies.length)))
        else Resource.pure[F, Int](-1)
    } yield if (lineSelectedIndex == -1) None else Some(youtubeLinkReplies(lineSelectedIndex))
  }

  object TriggerListCommand {

    def triggerListReplyBundleCommand[F[_]: Applicative](triggerFileUri: Uri): ReplyBundleCommand[F] =
      ReplyBundleCommand(
        trigger = CommandTrigger("triggerlist"),
        text = Some(
          TextReply(
            _ => Applicative[F].pure(List(s"Puoi trovare la lista dei trigger al seguente URL: $triggerFileUri")),
            true
          )
        )
      )
  }

  object TriggerSearchCommand {

    // TODO: Return the closest match on failure
    def triggerSearchReplyBundleCommand[F[_]: Applicative](
        botName: String,
        ignoreMessagePrefix: Option[String],
        mdr: List[ReplyBundleMessage[F]]
    ): ReplyBundleCommand[F] =
      ReplyBundleCommand(
        trigger = CommandTrigger("triggersearch"),
        text = Some(
          TextReply[F](
            m =>
              handleCommandWithInput[F](
                m,
                "triggersearch",
                botName,
                t =>
                  mdr
                    .collectFirstSome(replyBundle =>
                      replyBundle.trigger match {
                        case TextTrigger(textTriggers @ _*)
                            if MessageMatches.doesMatch(replyBundle, m, ignoreMessagePrefix) =>
                          Some(textTriggers.toList)
                        case _ => None
                      }
                    )
                    .fold(List(s"No matching trigger for $t"))((textTriggers: List[TextTriggerValue]) =>
                      textTriggers.map(_.toString)
                    )
                    .pure[F],
                """Input Required: Insert the test keyword to check if it's in some bot trigger"""
              ),
            false
          )
        )
      )

  }

  def handleCommandWithInput[F[_]: Applicative](
      msg: Message,
      command: String,
      botName: String,
      computation: String => F[List[String]],
      defaultReply: String
  ): F[List[String]] =
    msg.text
      .filterNot(t => t.trim == s"/$command" || t.trim == s"/$command@$botName")
      .map(t => computation(t.dropWhile(_ != ' ').tail))
      .getOrElse(List(defaultReply).pure[F])
}