package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object SurfacesView extends View:
  def apply(poolId: Long, model: Model[Surface], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case SurfacesListed(surfaces: List[Surface]) =>
          clearErrors()
          model.setEntities(surfaces)
        case _ => log(s"Surfaces -> handler failed: $event")

    div(
      bar(
        btn("Pool").amend {
          onClick --> { _ =>
            log("Surfaces -> Pool menu item onClick")
            route(PoolPage(poolId))
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListSurfaces(license, poolId)
          call(command, handler)
        },
        hdr("Surfaces"),
        err(errorBus),
        listview(
          split(model.entitiesVar, (id: Long) => SurfacePage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Surfaces -> New button onClick")
            route(SurfacePage(poolId, model.selectedEntityVar.now().id))
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Surfaces -> Refresh button onClick")
            val command = ListSurfaces(license, poolId)
            call(command, handler)
          }
        }
      )
    )