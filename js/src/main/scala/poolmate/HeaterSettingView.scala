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
          route(HeaterPage(heaterId))
        case _ => log(s"HeaterSetting -> add handler failed: $event")

    def updateHandler(event: Event): Unit = ???

    div(
      
    )