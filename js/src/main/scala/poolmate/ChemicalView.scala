package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object ChemicalView extends View:
  def apply(model: Model[Chemical], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case ChemicalAdded(chemical) =>
          clearErrors()
          model.addEntity(chemical)
          route(ChemicalsPage)
        case _ => emitError(s"Chemical add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(cause)
        case Updated(_) =>
          clearErrors()
          route(ChemicalsPage)
        case _ => emitError(s"Chemicals update handler failed: $event")

    div(
      bar(
        btn("Chemicals").amend {
          onClick --> { _ =>
            log("Chemical -> Chemicals menu item onClick")
            route(ChemicalsPage)
          }
        }
      ),
      div(
        hdr("Chemical"),
        err(errorBus),
        lbl("Type Of"),
        listbox( TypeOfChemical.toList ).amend {
          controlled(
            value <-- model.selectedEntityVar.signal.map(_.unit),
            onChange.mapToValue --> { value =>
              model.updateSelectedEntity( model.selectedEntityVar.now().copy(chemical = value) )
            }
          )
        },
        lbl("Amount"),
        dbl.amend {
          controlled(
            value <-- model.selectedEntityVar.signal.map(_.amount.toString),
            onInput.mapToValue.filter(_.toDoubleOption.nonEmpty).map(_.toDouble) --> { value =>
              model.updateSelectedEntity( model.selectedEntityVar.now().copy(amount = value) )
            }
          )
        },
        lbl("Unit"),
        listbox( UnitOfMeasure.toList ).amend {
          controlled(
            value <-- model.selectedEntityVar.signal.map(_.unit),
            onChange.mapToValue --> { value =>
              model.updateSelectedEntity( model.selectedEntityVar.now().copy(unit = value) )
            }
          )
        },
        lbl("Added"),
        date.amend {
          controlled(
            value <-- model.selectedEntityVar.signal.map(chemical => localDateOfLongToString(chemical.added)),
            onInput.mapToValue.filter(_.nonEmpty) --> { added =>
              model.updateSelectedEntity( model.selectedEntityVar.now().copy(added = localDateOfStringToLong(added)) )
            }
          )
        },
      ),
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { cleaning => !(cleaning.id.isZero && cleaning.isValid) }
          onClick --> { _ =>
            log(s"Chemical -> Add onClick")
            val command = AddChemical(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { cleaning => !(cleaning.id.isGreaterThanZero && cleaning.isValid) }
          onClick --> { _ =>
            log(s"Chemical -> Update onClick")
            val command = UpdateChemical(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )