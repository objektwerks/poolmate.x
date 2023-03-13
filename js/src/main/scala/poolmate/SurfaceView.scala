package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Validator.*

object SurfaceView extends View:
  def apply(model: Model[Surface], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add surface failed: $cause")
        case SurfaceAdded(surface) =>
          clearErrors()
          model.addEntity(surface)
          route(SurfacesPage)
        case _ => log(s"Surface -> add handler failed: $event")

    def updateHandler(event: Event): Unit = ???

    div(
      
    )