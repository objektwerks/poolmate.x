package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object RepairView extends View:
  def apply(model: Model[Repair], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add repair failed: $cause")
        case RepairAdded(repair) =>
          clearErrors()
          model.addEntity(repair)
          route(RepairsPage)
        case _ => log(s"Repair -> add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update repair failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(RepairsPage)
        case _ => log(s"Repair -> update handler failed: $event")

    div(
      bar(
        btn("Repairs").amend {
          onClick --> { _ =>
            log("Repair -> Repairs menu item onClick")
            route(RepairsPage)
          }
        },
      ),
      hdr("Repair"),
      err(errorBus),
      lbl("Repair"),
      txt.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(_.repair),
          onChange.mapToValue.filter(_.nonEmpty) --> { repair =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(repair = repair) )
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
      lbl("Repaired"),
      date.amend {
        controlled(
          value <-- model.selectedEntityVar.signal.map(repair => localDateOfLongToString(repair.repaired)),
          onInput.mapToValue.filter(_.nonEmpty) --> { repaired =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(repaired = localDateOfStringToLong(repaired)) )
          }
        )
      },
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { repair => !(repair.id.isZero && repair.isValid) }
          onClick --> { _ =>
            log(s"Repair -> Add onClick")
            val command = AddRepair(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { repair => !(repair.id.isGreaterThanZero && repair.isValid) }
          onClick --> { _ =>
            log(s"Repair -> Update onClick")
            val command = UpdateRepair(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )