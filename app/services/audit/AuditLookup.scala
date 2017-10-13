package services.audit

import models.result.data.DataFieldModel
import services.ServiceRegistry
import services.audit.AuditArgs._
import util.tracing.TraceData

import scala.concurrent.Future

@javax.inject.Singleton
class AuditLookup @javax.inject.Inject() (registry: ServiceRegistry) {
  def getById(key: String, id: Seq[String])(implicit traceData: TraceData) = getModel(key, getArg(id, _))

  private[this] def getModel(key: String, arg: (Int) => String)(implicit traceData: TraceData): Future[Option[DataFieldModel]] = key.toLowerCase match {
    /* Start registry lookups */
    /* End registry lookups */

    case "user" => registry.userService.getByPrimaryKey(uuidArg(arg(0)))
    case _ => throw new IllegalStateException(s"Invalid model key [$key].")
  }
}
