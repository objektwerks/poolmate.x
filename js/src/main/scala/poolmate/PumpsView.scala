package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object PumpsView extends View:
  def apply(poolId: Long, model: Model[Pump], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case PumpsListed(pumps: List[Pump]) =>
          clearErrors()
          model.setEntities(pumps)
        case _ => log(s"Pumps -> handler failed: $event")

    div(
      bar(
        btn("Pool").amend {
          onClick --> { _ =>
            log("Pumps -> Pool menu item onClick")
            route(PoolPage(poolId))
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListPumps(license, poolId)
          call(command, handler)
        },
        hdr("Pumps"),
        err(errorBus),
        listview(
          split(model.entitiesVar, (id: Long) => PumpPage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Pumps -> New button onClick")
            route(PumpPage(poolId, model.selectedEntityVar.now().id))
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Pumps -> Refresh button onClick")
            val command = ListPumps(license, poolId)
            call(command, handler)
          }
        }
      )
    )