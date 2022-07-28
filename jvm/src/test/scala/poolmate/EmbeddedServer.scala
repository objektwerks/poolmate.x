package poolmate

import cask.main.Main

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._

import io.undertow.Undertow
import io.undertow.server.handlers.BlockingHandler

final class EmbeddedServer(conf: Config) extends Main with LazyLogging:
  val _host = conf.getString("host")
  val _port = conf.getInt("port")
  
  val store = Store(conf, Store.cache(minSize = 4, maxSize = 10, expireAfter = 24.hour))
  val emailSender = EmailSender(conf, store)
  val service = Service(store)
  val authorizer = Authorizer(service)
  val validator = Validator()
  val dispatcher = Dispatcher(authorizer, validator, service, emailSender)
  val router = Router(dispatcher, store)
  override val allRoutes = Seq(router)

  Main.silenceJboss()    
  val server = Undertow.builder
    .addHttpListener(port, host)
    .setHandler(defaultHandler)
    .build
  
  override def host: String = _host

  override def port: Int = _port

  override def defaultHandler: BlockingHandler =
    new BlockingHandler( CorsHandler(dispatchTrie,
                                     mainDecorators,
                                     debugMode = false,
                                     handleNotFound,
                                     handleMethodNotAllowed,
                                     handleEndpointError) )

  def start(): Unit =
    server.start()
    logger.info(s"*** EmbeddedServer started at http://$_host:$_port/")

  def stop(): Unit =
    server.stop()
    logger.info("*** EmbeddedServer stopped.")