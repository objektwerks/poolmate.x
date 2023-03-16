package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object TimerSettingsView extends View:
  def apply(poolId: Long, timerId: Long, model: Model[TimerSetting], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case TimerSettingsListed(timersettings: List[TimerSetting]) =>
          clearErrors()
          model.setEntities(timersettings)
        case _ => emitError(s"TimerSettings handler failed: $event")

    div(
      bar(
        btn("Timer").amend {
          onClick --> { _ =>
            log("TimerSettings -> Timer menu item onClick")
            route(TimerPage(poolId, timerId))
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListTimerSettings(license, timerId)
          call(command, handler)
        },
        hdr("TimerSettings"),
        err(errorBus),
        listview(
          split(model.entitiesVar, (id: Long) => TimerSettingPage(poolId, timerId))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"TimerSettings -> New button onClick")
            route(TimerSettingPage(poolId, timerId, model.selectedEntityVar.now().id))
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"TimerSettings -> Refresh button onClick")
            val command = ListTimerSettings(license, poolId)
            call(command, handler)
          }
        }
      )
    )