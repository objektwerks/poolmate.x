package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object DeckView extends View:
  def apply(model: Model[Deck], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add surface failed: $cause")
        case DeckAdded(deck) =>
          clearErrors()
          model.addEntity(deck)
          route(DecksPage)
        case _ => log(s"Deck -> add handler failed: $event")

    def updateHandler(event: Event): Unit = ???

    div(
      
    )