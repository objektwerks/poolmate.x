package poolmate

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.sys.process.Process

import Serializer.given

import upickle.default.*

class RequesterTest extends AnyFunSuite with Matchers with LazyLogging:
  Process("psql -d poolmate -f ddl.sql").run().exitValue()

  val conf = ConfigFactory.load("test.server.conf")
  val host = conf.getString("host")
  val port = conf.getString("port")
  val url = s"http://$host:$port/command"
  val server = EmbeddedServer(conf)

  test("requester") {
    server.start()

    val register = Register(emailAddress = conf.getString("email.to"))
    logger.info(s"*** Register: $register")

    val registerJson = write[Register](register)
    logger.info(s"*** Register json: $registerJson")

    val registerResponse = requests.post(url, data = registerJson)
    logger.info(s"*** Register response: $registerResponse")
    
    val registered = read[Registered](registerResponse.text())
    logger.info(s"*** Registered: $registered")

    val login = Login(registered.account.emailAddress, registered.account.pin)
    logger.info(s"*** Login: $login")

    val loginJson = write[Login](login)
    logger.info(s"*** Login json: $loginJson")

    val loginResponse = requests.post(url, data = loginJson)
    logger.info(s"*** Login response: $loginResponse")

    val loggedIn = read[LoggedIn](loginResponse.text())
    logger.info(s"*** LoggedIn: $loggedIn")

    require(registered.account == loggedIn.account, "Registered account not equal to LoggedIn account.")

    server.stop()
  }