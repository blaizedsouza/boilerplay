package services.socket

import models.RequestMessage
import services.event.{AuditEventHandler, EventHandler}
import services.{InitService, Logging}
import util.{BinarySerializers, JsonSerializers}

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("SocketConnection")
class SocketConnection(path: String, binary: Boolean = false) {
  private[this] val eventHandler = path match {
    case "/connect" => new EventHandler {}
    case "/admin/audit/activity/connect" => new AuditEventHandler
    case _ => throw new IllegalStateException(s"Unhandled path argument [$path].")
  }

  private[this] val socket = new NetworkSocket(eventHandler)

  InitService.initIfNeeded()
  NetworkMessage.register(sendMessage)
  Logging.debug(util.Config.projectName + " is connecting...")
  connect(path)

  protected def connect(path: String) = {
    val loc = org.scalajs.dom.document.location
    val wsProtocol = if (loc.protocol == "https:") { "wss" } else { "ws" }
    val socketUrl = s"$wsProtocol://${loc.host}$path"
    socket.open(socketUrl)
  }

  def sendMessage(rm: RequestMessage): Unit = {
    if (socket.isConnected) {
      if (binary) {
        socket.sendBinary(BinarySerializers.writeRequestMessage(rm))
      } else {
        socket.sendString(JsonSerializers.writeRequestMessage(rm, debug = true))
      }
    } else {
      throw new IllegalStateException("Not connected.")
    }
  }
}
