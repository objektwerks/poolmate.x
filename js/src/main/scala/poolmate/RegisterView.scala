package poolmate

import com.raquo.laminar.api.L.*

import Components.*
import Error.*
import Message.*
import Validators.*

object RegisterView extends View:
  def apply(emailAddressVar: Var[String], pinVar: Var[String], accountVar: Var[Account]): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(_, _, _, cause) => emitError(s"Register failed: $cause")
        case Registered(account) =>
          clearErrors()
          accountVar.set(account)
          pinVar.set(account.pin)
          route(LoginPage)
        case _ => log(s"Register -> handler failed: $event")
      
    div(
      hdr("Register"),
      err(errorBus),
      info(registerMessage),
      err(errorBus),
      lbl("Email Address"),
      email.amend {
        value <-- emailAddressVar
        onInput.mapToValue.filter(_.nonEmpty).setAsValue --> emailAddressVar
      },
      cbar(
        btn("Register").amend {
          disabled <-- emailAddressVar.signal.map(email => !email.isEmailAddress)
          onClick --> { _ =>
            log(s"Register button onClick -> email address: ${emailAddressVar.now()}")
            val command = Register(emailAddressVar.now())
            call(command, handler)
          }
        },
      )
    )