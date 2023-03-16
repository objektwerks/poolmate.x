package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object DecksView extends View:
  def apply(poolId: Long, model: Model[Deck], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case DecksListed(decks: List[Deck]) =>
          clearErrors()
          model.setEntities(decks)
        case _ => emitError(s"Decks handler failed: $event")

    div(
      bar(
        btn("Pool").amend {
          onClick --> { _ =>
            log("Decks -> Pool menu item onClick")
            route(PoolPage(poolId))
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListDecks(license, poolId)
          call(command, handler)
        },
        hdr("Decks"),
        err(errorBus),
        listview(
          split(model.entitiesVar, (id: Long) => DeckPage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Decks -> New button onClick")
            route(DeckPage(poolId, model.selectedEntityVar.now().id))
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Decks -> Refresh button onClick")
            val command = ListDecks(license, poolId)
            call(command, handler)
          }
        }
      )
    )