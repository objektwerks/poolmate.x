package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object DeckView extends View:
  def apply(model: Model[Deck], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add deck failed: $cause")
        case DeckAdded(deck) =>
          clearErrors()
          model.addEntity(deck)
          route(DecksPage)
        case _ => log(s"Deck -> add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update deck failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(DecksPage)
        case _ => log(s"Deck -> update handler failed: $event")

    div(
      bar(
        btn("Decks").amend {
          onClick --> { _ =>
            log("Surface -> Decks menu item onClick")
            route(DecksPage)
          }
        },
      ),
      hdr("Deck"),
      err(errorBus),
      lbl("Installed"),
      date.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(deck => localDateOfLongToString(deck.installed)),
          onInput.mapToValue.filter(_.nonEmpty) --> { installed =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(installed = localDateOfStringToLong(installed)) )
          }
        )
      },
      lbl("Kind"),
      txt.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(_.kind),
          onChange.mapToValue.filter(_.nonEmpty) --> { kind =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(kind = kind) )
          }
        )
      },
      lbl("Cost"),
      int.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(_.cost.toString),
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { cost =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(cost = cost) )
          }
        )
      },
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { deck => !(deck.id.isZero && deck.isValid) }
          onClick --> { _ =>
            log(s"Deck -> Add onClick")
            val command = AddDeck(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { deck => !(deck.id.isGreaterThanZero && deck.isValid) }
          onClick --> { _ =>
            log(s"Deck -> Update onClick")
            val command = UpdateDeck(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )