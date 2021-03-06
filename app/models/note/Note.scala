package models.note

import java.time.LocalDateTime
import java.util.UUID

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.java8.time._
import models.result.data.{DataField, DataFieldModel, DataSummary}

object Note {
  implicit val jsonEncoder: Encoder[Note] = deriveEncoder
  implicit val jsonDecoder: Decoder[Note] = deriveDecoder

  def empty(author: Option[UUID] = None) = Note(
    id = UUID.randomUUID,
    relType = Some(""),
    relPk = Some(""),
    text = "",
    author = author.getOrElse(UUID.randomUUID),
    created = util.DateUtils.now
  )
}

case class Note(
    id: UUID = UUID.randomUUID(),
    relType: Option[String],
    relPk: Option[String],
    text: String,
    author: UUID,
    created: LocalDateTime = util.DateUtils.now
) extends DataFieldModel {
  override def toDataFields = Seq(
    DataField("id", Some(id.toString)),
    DataField("relType", relType.map(_.toString)),
    DataField("relPk", relPk.map(_.toString)),
    DataField("text", Some(text.toString)),
    DataField("author", Some(author.toString)),
    DataField("created", Some(created.toString))
  )

  def toSummary = DataSummary(model = "note", pk = Seq(id.toString), title = s"$relType / $relPk / $text ($id)")
}
