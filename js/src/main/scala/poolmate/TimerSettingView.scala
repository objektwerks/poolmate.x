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
          route(TimerSettingsPage)
        case _ => emitError(s"TimerSetting add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update timer setting failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(TimerSettingsPage)
        case _ => emitError(s"TimerSetting update handler failed: $event")

    div(
      bar(
        btn("Timer Settings").amend {
          onClick --> { _ =>
            log("TimerSetting -> Timer Settings menu item onClick")
            route(TimerSettingsPage)
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
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(created = localDateOfStringToLong(created)) )
          }
        )
      },
      lbl("Time On"),
      time.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(timersetting => localTimeOfLongToString(timersetting.timeOn)),
          onChange.mapToValue.filter(_.nonEmpty) --> { timeOn =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(timeOn = localTimeOfStringToInt(timeOn)) )
          }
        )
      },
      lbl("Time Off"),
      time.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(timersetting => localTimeOfLongToString(timersetting.timeOff)),
          onInput.mapToValue.filter(_.nonEmpty) --> { timeOff =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(timeOff = localTimeOfStringToInt(timeOff)) )
          }
        )
      },
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { timersetting => !(timersetting.id.isZero && timersetting.isValid) }
          onClick --> { _ =>
            log(s"TimerSetting -> Add onClick")
            val command = AddTimerSetting(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { timersetting => !(timersetting.id.isGreaterThanZero && timersetting.isValid) }
          onClick --> { _ =>
            log(s"TimerSetting -> Update onClick")
            val command = UpdateTimerSetting(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )