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
        case _ => emitError(s"HeaterSettings -> handler failed: $event")

    div(
      bar(
        btn("Heater").amend {
          onClick --> { _ =>
            log("HeaterSettings -> Heater menu item onClick")
            route(HeaterPage(poolId, heaterId))
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListHeaterSettings(license, heaterId)
          call(command, handler)
        },
        hdr("HeaterSettings"),
        err(errorBus),
        listview(
          split(model.entitiesVar, (id: Long) => HeaterSettingPage(poolId, heaterId))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"HeaterSettings -> New button onClick")
            route(HeaterSettingPage(poolId, heaterId, model.selectedEntityVar.now().id))
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"HeaterSettings -> Refresh button onClick")
            val command = ListHeaterSettings(license, poolId)
            call(command, handler)
          }
        }
      )
    )