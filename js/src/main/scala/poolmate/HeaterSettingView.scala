package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object HeaterSettingView extends View:
  def apply(heaterId: Long, model: Model[HeaterSetting], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add heater setting failed: $cause")
        case HeaterSettingAdded(heatersetting) =>
          clearErrors()
          model.addEntity(heatersetting)
          route(HeaterSettingsPage)
        case _ => emitError(s"HeaterSetting add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update heater setting failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(HeaterSettingsPage)
        case _ => emitError(s"HeaterSetting update handler failed: $event")

    div(
      bar(
        btn("Heater Settings").amend {
          onClick --> { _ =>
            log("HeaterSetting -> Heater Settings menu item onClick")
            route(HeaterSettingsPage)
          }
        },
      ),
      hdr("Heater Setting"),
      err(errorBus),
      lbl("Temperature"),
      int.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(_.temp.toString),
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { temp =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(temp = temp) )
          }
        )
      },
      lbl("Date On"),
      date.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(heatersetting => localDateOfLongToString(heatersetting.dateOn)),
          onInput.mapToValue.filter(_.nonEmpty) --> { dateOn =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(dateOn = localDateOfStringToLong(dateOn)) )
          }
        )
      },
      lbl("Date Off"),
      date.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(heatersetting => localDateOfLongToString(heatersetting.dateOff)),
          onInput.mapToValue.filter(_.nonEmpty) --> { dateOff =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(dateOff = localDateOfStringToLong(dateOff)) )
          }
        )
      },
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { heatersetting => !(heatersetting.id.isZero && heatersetting.isValid) }
          onClick --> { _ =>
            log(s"HeaterSetting -> Add onClick")
            val command = AddHeaterSetting(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { heater => !(heater.id.isGreaterThanZero && heater.isValid) }
          onClick --> { _ =>
            log(s"HeaterSetting -> Update onClick")
            val command = UpdateHeaterSetting(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )