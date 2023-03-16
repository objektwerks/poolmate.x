package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object SuppliesView extends View:
  def apply(poolId: Long, model: Model[Supply], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case SuppliesListed(supplies: List[Supply]) =>
          clearErrors()
          model.setEntities(supplies)
        case _ => emitError(s"Supplies handler failed: $event")

    div(
      bar(
        btn("Pool").amend {
          onClick --> { _ =>
            log("Supplies -> Pool menu item onClick")
            route(PoolPage(poolId))
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListSupplies(license, poolId)
          call(command, handler)
        },
        hdr("Supplies"),
        err(errorBus),
        listview(
          split(model.entitiesVar, (id: Long) => SupplyPage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Supplies -> New button onClick")
            route(SupplyPage(poolId, model.selectedEntityVar.now().id))
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Supplies -> Refresh button onClick")
            val command = ListSupplies(license, poolId)
            call(command, handler)
          }
        }
      )
    )