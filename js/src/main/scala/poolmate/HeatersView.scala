package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object HeatersView extends View:
  def apply(poolId: Long, model: Model[Heater], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case HeatersListed(heaters: List[Heater]) =>
          clearErrors()
          model.setEntities(heaters)
        case _ => log(s"Heaters -> handler failed: $event")

    div(
      bar(
        btn("Pool").amend {
          onClick --> { _ =>
            log("Heaters -> Pool menu item onClick")
            route(PoolPage(poolId))
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListHeaters(license, poolId)
          call(command, handler)
        },
        hdr("Heaters"),
        err(errorBus),
        listview(
          split(model.entitiesVar, (id: Long) => HeaterPage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Heaters -> New button onClick")
            route(HeaterPage(poolId, model.selectedEntityVar.now().id))
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Heaters -> Refresh button onClick")
            val command = ListHeaters(license, poolId)
            call(command, handler)
          }
        }
      )
    )