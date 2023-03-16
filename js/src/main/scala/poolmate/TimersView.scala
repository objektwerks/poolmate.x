package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object TimersView extends View:
  def apply(poolId: Long, model: Model[Timer], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case TimersListed(timers: List[Timer]) =>
          clearErrors()
          model.setEntities(timers)
        case _ => emitError(s"Timers handler failed: $event")

    div(
      bar(
        btn("Pool").amend {
          onClick --> { _ =>
            log("Timers -> Pool menu item onClick")
            route(PoolPage(poolId))
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListTimers(license, poolId)
          call(command, handler)
        },
        hdr("Timers"),
        err(errorBus),
        listview(
          split(model.entitiesVar, (id: Long) => TimerPage(id))
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