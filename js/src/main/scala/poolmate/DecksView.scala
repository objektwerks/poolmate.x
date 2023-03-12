package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Validator.*

object DecksView extends View:
  def apply(poolId: Long, model: Model[Deck], license: String): HtmlElement =
    def handler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case DecksListed(decks: List[Deck]) =>
          clearErrors()
          model.setEntities(decks)
        case _ => log(s"Decks -> handler failed: $event")

    div(
      
    )