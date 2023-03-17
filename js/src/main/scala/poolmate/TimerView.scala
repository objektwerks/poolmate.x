package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object TimerView extends View:
  def apply(model: Model[Timer], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add timer failed: $cause")
        case TimerAdded(timer) =>
          clearErrors()
          model.addEntity(timer)
          route(TimersPage)
        case _ => emitError(s"Timer add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update timer failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(TimersPage)
        case _ => emitError(s"Timer update handler failed: $event")

    div(
      bar(
        btn("Timers").amend {
          onClick --> { _ =>
            log("Timer -> Timers menu item onClick")
            route(TimersPage)
          }
        },
        btn("Timer Settings").amend {
          onClick --> { _ =>
            log("Timer Settings menu item onClick")
            route(TimerSettingsPage)
          }
        }
      ),
      hdr("Timer"),
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
          value <-- model.selectedEntityVar.signal.map(timer => localDateOfLongToString(timer.installed)),
          onInput.mapToValue.filter(_.nonEmpty) --> { installed =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(installed = localDateOfStringToLong(installed)) )
          }
        )
      },
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { timer => !(timer.id.isZero && timer.isValid) }
          onClick --> { _ =>
            log(s"Timer -> Add onClick")
            val command = AddTimer(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { timer => !(timer.id.isGreaterThanZero && timer.isValid) }
          onClick --> { _ =>
            log(s"Timer -> Update onClick")
            val command = UpdateTimer(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )