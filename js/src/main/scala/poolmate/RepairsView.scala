package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object RepairsView extends View:
  def apply(poolId: Long, model: Model[Repair], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case RepairsListed(repairs: List[Repair]) =>
          clearErrors()
          model.setEntities(repairs)
        case _ => log(s"Repairs -> handler failed: $event")

    div(
      bar(
        btn("Pool").amend {
          onClick --> { _ =>
            log("Repairs -> Pool menu item onClick")
            route(PoolPage(poolId))
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListRepairs(license, poolId)
          call(command, handler)
        },
        hdr("Repairs"),
        err(errorBus),
        listview(
          split(model.entitiesVar, (id: Long) => RepairPage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Repairs -> New button onClick")
            route(RepairPage(poolId, model.selectedEntityVar.now().id))
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Repairs -> Refresh button onClick")
            val command = ListRepairs(license, poolId)
            call(command, handler)
          }
        }
      )
    )