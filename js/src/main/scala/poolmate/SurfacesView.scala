package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Validator.*

object SurfacesView extends View:
  def apply(poolId: Long, model: Model[Surface], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case SurfacesListed(surfaces: List[Surface]) =>
          clearErrors()
          model.setEntities(surfaces)
        case _ => log(s"Surfaces -> handler failed: $event")

    div(
      
    )