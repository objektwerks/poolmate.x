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
      bar(
        btn("Heater").amend {
          onClick --> { _ =>
            log("HeaterSettings -> Pool menu item onClick")
            route(HeaterPage(heaterId))
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