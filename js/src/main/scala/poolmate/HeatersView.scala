package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object HeatersView extends View:
  def apply(poolId: Long, model: Model[Heater], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case HeatersListed(heaters: List[Heater]) =>
          clearErrors()
          model.setEntities(heaters)
        case _ => log(s"Heaters -> handler failed: $event")

    div(
      
    )