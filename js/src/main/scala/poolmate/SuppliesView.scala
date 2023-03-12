package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object SuppliesView extends View:
  def apply(poolId: Long, model: Model[Supply], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case SuppliesListed(supplies: List[Supply]) =>
          clearErrors()
          model.setEntities(supplies)
        case _ => log(s"Supplies -> handler failed: $event")

    div(
      
    )