package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object HeaterView extends View:
  def apply(model: Model[Heater], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add heater failed: $cause")
        case HeaterAdded(heater) =>
          clearErrors()
          model.addEntity(heater)
          route(HeatersPage)
        case _ => log(s"Heater -> add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update heater failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(HeatersPage)
        case _ => log(s"Heater -> update handler failed: $event")

    div(
      
    )