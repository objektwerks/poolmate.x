package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object HeaterSettingsView extends View:
  def apply(poolId: Long, heaterId: Long, model: Model[HeaterSetting], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case HeaterSettingsListed(heatersettings: List[HeaterSetting]) =>
          clearErrors()
          model.setEntities(heatersettings)
        case _ => log(s"TimerSettings -> handler failed: $event")

    div(
      
    )