package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object TimerSettingsView extends View:
  def apply(poolId: Long, timerId: Long, model: Model[TimerSetting], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case TimerSettingsListed(timerSettings: List[TimerSetting]) =>
          clearErrors()
          model.setEntities(timerSettings)
        case _ => log(s"TimerSettings -> handler failed: $event")

    div(
      bar(
        btn("Timer").amend {
          onClick --> { _ =>
            log("TimerSettings -> Pool menu item onClick")
            route(TimerPage(timerId))
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
            log(s"Timers -> New button onClick")
            route(TimerPage(poolId, model.selectedEntityVar.now().id))
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Timers -> Refresh button onClick")
            val command = ListTimers(license, poolId)
            call(command, handler)
          }
        }
      )
    )