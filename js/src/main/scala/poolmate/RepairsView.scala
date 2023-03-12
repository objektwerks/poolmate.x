package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object RepairsView extends View:
  def apply(poolId: Long, model: Model[Repair], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case RepairsListed(repairs: List[Repair]) =>
          clearErrors()
          model.setEntities(repairs)
        case _ => log(s"Repairs -> handler failed: $event")

    div(
      
    )