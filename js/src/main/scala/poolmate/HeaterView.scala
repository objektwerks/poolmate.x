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
        case _ => emitError(s"Heater add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update heater failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(HeatersPage)
        case _ => emitError(s"Heater update handler failed: $event")

    div(
      bar(
        btn("Heaters").amend {
          onClick --> { _ =>
            log("Heater -> Heaters menu item onClick")
            route(HeatersPage)
          }
        },
      ),
      hdr("Heater"),
      err(errorBus),
      lbl("Model"),
      txt.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(_.model),
          onChange.mapToValue.filter(_.nonEmpty) --> { m =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(model = m) )
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
      lbl("Installed"),
      date.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(heater => localDateOfLongToString(heater.installed)),
          onInput.mapToValue.filter(_.nonEmpty) --> { installed =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(installed = localDateOfStringToLong(installed)) )
          }
        )
      },
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { heater => !(heater.id.isZero && heater.isValid) }
          onClick --> { _ =>
            log(s"Heater -> Add onClick")
            val command = AddHeater(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { heater => !(heater.id.isGreaterThanZero && heater.isValid) }
          onClick --> { _ =>
            log(s"Heater -> Update onClick")
            val command = UpdateHeater(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )