package poolmate

import com.raquo.laminar.api.L._

import org.scalajs.dom
import org.scalajs.dom.Headers
import org.scalajs.dom.HttpMethod
import org.scalajs.dom.RequestInit
import org.scalajs.dom.console.log

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Thenable.Implicits.*
import scala.util.control.NonFatal

import Serializer.given

import upickle.default.{read, write}

object Proxy:
  private val hdrs = new Headers {
    js.Array(
      js.Array("Content-Type", "application/json; charset=utf-8"),
      js.Array("Accept", "application/json")
    )
  }

  private val params = new RequestInit {
    method = HttpMethod.POST
    headers = hdrs
  }

  def now: Future[String] =
    ( 
      for
        response <- dom.fetch(Url.now)
        text     <- response.text()
      yield text
    ).recover {
      case failure: Exception => s"Now failed: ${failure.getMessage}"
    }

  def call(command: Command,
           handler: Event => Unit) =
    val event = post(command)
    handle(event, handler)

  private def post(command: Command): Future[Event] =
    log(s"Proxy:post command: $command")
    params.body = write[Command](command)
    log(s"Proxy:post params: $params")
    (
      for
        response <- dom.fetch(Url.command, params)
        json     <- response.text()
      yield
        log(s"Proxy:post json: $json")
        read[Event](json)
    ).recover {
      case NonFatal(failure) =>
        log(s"Proxy:post failure: ${failure.getCause}")
        Fault(failure)
    }

  private def handle(future: Future[Event],
                     handler: Event => Unit): Unit =
    future map { event =>
      log(s"Proxy:handle event: $event")
      handler(event)
    }