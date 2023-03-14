package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object SupplyView extends View:
  def apply(model: Model[Supply], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add supply failed: $cause")
        case SupplyAdded(supply) =>
          clearErrors()
          model.addEntity(supply)
          route(SuppliesPage)
        case _ => log(s"Supply -> add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update supply failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(SuppliesPage)
        case _ => log(s"Supply -> update handler failed: $event")

    div(
      bar(
        btn("Supplies").amend {
          onClick --> { _ =>
            log("Supply -> Supplies menu item onClick")
            route(SuppliesPage)
          }
        },
      ),
      hdr("Supply"),
      err(errorBus),
      lbl("Item"),
      txt.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(_.item),
          onChange.mapToValue.filter(_.nonEmpty) --> { item =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(item = item) )
          }
        )
      },
      lbl("Amount"),
      dbl.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(_.amount.toString),
          onInput.mapToValue.filter(_.toDoubleOption.nonEmpty).map(_.toDouble) --> { amount =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(amount = amount) )
          }
        )
      },
      lbl("Unit"),
      listbox( UnitOfMeasure.toList ).amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(_.unit),
          onChange.mapToValue --> { unit =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(unit = unit) )
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
      lbl("Purchase"),
      date.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(supply => localDateOfLongToString(supply.purchased)),
          onInput.mapToValue.filter(_.nonEmpty) --> { purchased =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(purchased = localDateOfStringToLong(purchased)) )
          }
        )
      },
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { supply => !(supply.id.isZero && supply.isValid) }
          onClick --> { _ =>
            log(s"Supply -> Add onClick")
            val command = AddSupply(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { supply => !(supply.id.isGreaterThanZero && supply.isValid) }
          onClick --> { _ =>
            log(s"Supply -> Update onClick")
            val command = UpdateSupply(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )