package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object PumpsView extends View:
  def apply(poolId: Long, model: Model[Pump], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case PumpsListed(pumps: List[Pump]) =>
          clearErrors()
          model.setEntities(pumps)
        case _ => log(s"Pumps -> handler failed: $event")

    div(
      
    )