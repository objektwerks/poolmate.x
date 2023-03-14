package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object RepairView extends View:
  def apply(model: Model[Repair], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add repair failed: $cause")
        case RepairAdded(repair) =>
          clearErrors()
          model.addEntity(repair)
          route(RepairsPage)
        case _ => log(s"Repair -> add handler failed: $event")

    def updateHandler(event: Event): Unit = ???

    div(
      
    )