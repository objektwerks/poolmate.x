package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object PumpView extends View:
  def apply(model: Model[Pump], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add pump failed: $cause")
        case PumpAdded(pump) =>
          clearErrors()
          model.addEntity(pump)
          route(PumpsPage)
        case _ => emitError(s"Pump add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update pump failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(PumpsPage)
        case _ => emitError(s"Pump update handler failed: $event")

    div(
      bar(
        btn("Pumps").amend {
          onClick --> { _ =>
            log("Pump -> Pumps menu item onClick")
            route(PumpsPage)
          }
        },
      ),
      hdr("Pump"),
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
          value <-- model.selectedEntityVar.signal.map(pump => localDateOfLongToString(pump.installed)),
          onInput.mapToValue.filter(_.nonEmpty) --> { installed =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(installed = localDateOfStringToLong(installed)) )
          }
        )
      },
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { pump => !(pump.id.isZero && pump.isValid) }
          onClick --> { _ =>
            log(s"Pump -> Add onClick")
            val command = AddPump(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { pump => !(pump.id.isGreaterThanZero && pump.isValid) }
          onClick --> { _ =>
            log(s"Pump -> Update onClick")
            val command = UpdatePump(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )