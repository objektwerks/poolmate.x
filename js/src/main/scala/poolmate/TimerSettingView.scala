package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object TimerSettingView extends View:
  def apply(timerId: Long, model: Model[TimerSetting], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add timer setting failed: $cause")
        case TimerSettingAdded(timersetting) =>
          clearErrors()
          model.addEntity(timersetting)
          route(TimerPage(timerId))
        case _ => log(s"TimerSetting -> add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update timer setting failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(TimerPage(timerId))
        case _ => log(s"TimerSetting -> update handler failed: $event")

    div(
      bar(
        btn("Timer").amend {
          onClick --> { _ =>
            log("TimerSetting -> Timer menu item onClick")
            route(TimerPage(timerId))
          }
        },
      ),
      hdr("Timer Setting"),
      err(errorBus),
      lbl("Created"),
      date.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(timersetting => localDateOfLongToString(timersetting.created)),
          onInput.mapToValue.filter(_.nonEmpty) --> { created =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(installed = localDateOfStringToLong(created)) )
          }
        )
      },
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