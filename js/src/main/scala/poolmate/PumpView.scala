package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object PumpView extends View:
  def apply(model: Model[Pump], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add pump failed: $cause")
        case PumpAdded(pump) =>
          clearErrors()
          model.addEntity(pump)
          route(PumpsPage)
        case _ => log(s"Pump -> add handler failed: $event")

    def updateHandler(event: Event): Unit = ???

    div(
      
    )