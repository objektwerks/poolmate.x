package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object TimerView extends View:
  def apply(model: Model[Timer], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add timer failed: $cause")
        case TimerAdded(timer) =>
          clearErrors()
          model.addEntity(timer)
          route(TimersPage)
        case _ => log(s"Timer -> add handler failed: $event")

    def updateHandler(event: Event): Unit = ???

    div(
      
    )