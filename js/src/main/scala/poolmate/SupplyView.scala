package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object SupplyView extends View:
  def apply(model: Model[Supply], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add supply failed: $cause")
        case SupplyAdded(supply) =>
          clearErrors()
          model.addEntity(supply)
          route(SuppliesPage)
        case _ => log(s"Supply -> add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update supply failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(SuppliesPage)
        case _ => log(s"Supply -> update handler failed: $event")

    div(
      
    )