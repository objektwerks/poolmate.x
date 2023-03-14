package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Validator.*

object TimerSettingView extends View:
  def apply(timerId: Long, model: Model[TimerSetting], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add timer failed: $cause")
        case TimerSettingAdded(timersetting) =>
          clearErrors()
          model.addEntity(timersetting)
          route(TimerPage(timerId))
        case _ => log(s"TimerSetting -> add handler failed: $event")

    def updateHandler(event: Event): Unit = ???

    div(
      
    )