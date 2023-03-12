package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object TimerSettingsView extends View:
  def apply(poolId: Long, model: Model[TimerSetting], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case TimerSettingsListed(timerSettings: List[TimerSetting]) =>
          clearErrors()
          model.setEntities(timerSettings)
        case _ => log(s"TimerSettings -> handler failed: $event")

    div(
      
    )