package poolmate

import com.raquo.laminar.api.L.*

import java.time.LocalDate

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

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update surface failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(SurfacesPage)
        case _ => log(s"Surface -> update handler failed: $event")

    div(
      bar(
        btn("Surfaces").amend {
          onClick --> { _ =>
            log("Surface -> Surfaces menu item onClick")
            route(SurfacesPage)
          }
        },
      ),
      hdr("Surface"),
      err(errorBus),
      lbl("Installed"),
      date.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(surface => LocalDate.ofEpochDay(surface.installed).toString),
          onInput.mapToValue.filter(_.nonEmpty) --> { installed =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(installed = LocalDate.parse(installed).toEpochDay) )
          }
        )
      },
      lbl("Kind"),
      txt.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(_.kind),
          onChange.mapToValue --> { kind =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(kind = kind) )
          }
        )
      },
      lbl("Cost"),
      int.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(_.cost.toString),
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { cost =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(cost = cost) )
          }
        )
      },
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { surface => !(surface.id.isZero && surface.isValid) }
          onClick --> { _ =>
            log(s"Surface -> Add onClick")
            val command = AddSurface(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { surface => !(surface.id.isGreaterThanZero && surface.isValid) }
          onClick --> { _ =>
            log(s"Surface -> Update onClick")
            val command = UpdateSurface(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )