package models.queries.auth

import java.util.UUID

import models.database._
import models.queries.EngineHelper.quote

object UserSearchQueries {
  private[this] val engine = "postgres"
  private[this] val tableName = UserQueries.tableName

  case class IsUsernameInUse(name: String) extends SingleRowQuery[Boolean] {
    override val sql = s"""select count(*) as c from ${quote(tableName, engine)} where ${quote("username", engine)} = ?"""
    override val values = Seq(name)
    override def map(row: Row) = row.as[Long]("c") != 0L
  }

  case class GetUsername(id: UUID) extends Query[Option[String]] {
    override val name = s"user.search.get.username"
    override val sql = s"""select ${quote("username", engine)} from ${quote(tableName, engine)} where ${quote("id", engine)} = ?"""
    override val values = Seq(id)
    override def reduce(rows: Iterator[Row]) = rows.toSeq.headOption.map(_.as[String]("username"))
  }

  case class GetUsernameSeq(ids: Set[UUID]) extends Query[Map[UUID, String]] {
    override val name = s"user.search.get.username.seq"
    private[this] val idClause = ids.map("'" + _ + "'").mkString(", ")
    override val sql = s"""select ${quote("id", engine)}, ${quote("username", engine)}
      |from ${quote(tableName, engine)}
      |where ${quote("id", engine)} in ($idClause)
      |""".stripMargin.trim
    override def reduce(rows: Iterator[Row]) = rows.map(r => r.as[UUID]("id") -> r.as[String]("username")).toMap
  }

  case object CountAdmins extends SingleRowQuery[Int]() {
    override val name = s"user.search.count.admins"
    override val sql = s"select count(*) as c from ${quote(tableName, engine)} where ${quote("role", engine)} = 'admin'"
    override def map(row: Row) = row.as[Long]("c").toInt
  }
}
