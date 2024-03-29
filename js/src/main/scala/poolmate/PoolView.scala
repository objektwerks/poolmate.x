package poolmate

import com.raquo.laminar.api.L.*

import Component.*
import Entity.*
import Validator.*

object PoolView extends View:
  def apply(model: Model[Pool], license: String): HtmlElement =
    def addHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Add pool failed: $cause")
        case PoolAdded(pool) =>
          clearErrors()
          model.addEntity(pool)
          route(PoolsPage)
        case _ => emitError(s"Pool add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(cause, _) => emitError(s"Update pool failed: $cause")
        case Updated(_) =>
          clearErrors()
          route(PoolsPage)
        case _ => emitError(s"Pool update handler failed: $event")

    div(
      bar(
        btn("Pools").amend {
          onClick --> { _ =>
            log("Pool -> Pools menu item onClick")
            route(PoolsPage)
          }
        },
        dropdown(
          btn("Maintenance"),
          btn("Measurements").amend {
            onClick --> { _ =>
              log("Measurements menu item onClick")
              // route(MeasurementsPage)
            }
          },
          btn("Cleanings").amend {
            onClick --> { _ =>
              log("Cleanings menu item onClick")
              // route(CleaningsPage)
            }
          },
          btn("Chemicals").amend {
            onClick --> { _ =>
              log("Chemicals menu item onClick")
              // route(ChemicalsPage)
            }
          }
        ),
        dropdown(
          btn("Expenses"),
          btn("Supplies").amend {
            onClick --> { _ =>
              log("Supplies menu item onClick")
              route(SuppliesPage)
            }
          },
          btn("Repairs").amend {
            onClick --> { _ =>
              log("Repairs menu item onClick")
              route(RepairsPage)
            }
          }
        ),
        dropdown(
          btn("Hardware"),
          btn("Pumps").amend {
            onClick --> { _ =>
              log("Pumps menu item onClick")
              route(PumpsPage)
            }
          },
          btn("Timers").amend {
            onClick --> { _ =>
              log("Timers menu item onClick")
              route(TimersPage)
            }
          },
          btn("Heaters").amend {
            onClick --> { _ =>
              log("Heaters menu item onClick")
              route(HeatersPage)
            }
          }
        ),
        dropdown(
          btn("Aesthetics"),
          btn("Surfaces").amend {
            onClick --> { _ =>
              log("Surfaces menu item onClick")
              route(SurfacesPage)
            }
          },
          btn("Decks").amend {
            onClick --> { _ =>
              log("Decks menu item onClick")
              route(DecksPage)
            }
          },
        )
      ),
      div(
        hdr("Pool"),
        err(errorBus),
        lbl("Name"),
        txt.amend {
          controlled(
            value <-- model.selectedEntityVar.signal.map(_.name),
            onInput.mapToValue.filter(_.nonEmpty) --> { name =>
              model.updateSelectedEntity( model.selectedEntityVar.now().copy(name = name) )
            }
          )
        },
        lbl("Volume"),
        txt.amend {
          controlled(
            value <-- model.selectedEntityVar.signal.map(_.volume.toString),
            onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { volume =>
              model.updateSelectedEntity( model.selectedEntityVar.now().copy(volume = volume) )
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
        dbl.amend {
          controlled(
            value <-- model.selectedEntityVar.signal.map(_.cost.toString),
            onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { cost =>
              model.updateSelectedEntity( model.selectedEntityVar.now().copy(cost = cost) )
            }
          )
        },
        lbl("Built"),
        date.amend {
          controlled(
            value <-- model.selectedEntityVar.signal.map(pool => localDateOfLongToString(pool.built)),
            onInput.mapToValue.filter(_.nonEmpty) --> { built =>
              model.updateSelectedEntity( model.selectedEntityVar.now().copy(built = localDateOfStringToLong(built)) )
            }
          )
        }
      ),
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { pool => !(pool.id.isZero && pool.isValid) }
          onClick --> { _ =>
            log(s"Pool -> Add onClick")
            val command = AddPool(license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { pool => !(pool.id.isGreaterThanZero && pool.isValid) }
          onClick --> { _ =>
            log(s"Pool -> Update onClick")
            val command = UpdatePool(license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )