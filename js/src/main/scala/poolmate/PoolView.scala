package poolmate

import com.raquo.laminar.api.L.*

import Components.*
import Error.*
import Validators.*

object PoolView extends View:
  def apply(model: Model[Pool], accountVar: Var[Account]): HtmlElement =
    val nameErrorBus = new EventBus[String]
    val builtErrorBus = new EventBus[String]
    val volumeErrorBus = new EventBus[String]

    def addHandler(event: Event): Unit =
      event match
        case Fault(_, _, _, cause) => emitError(s"Add pool failed: $cause")
        case PoolAdded(pool) =>
          clearErrors()
          model.addEntity(pool)
          route(PoolsPage)
        case _ => log(s"Pool -> add handler failed: $event")

    def updateHandler(event: Event): Unit =
      event match
        case Fault(_, _, _, cause) => emitError(s"Update pool failed: $cause")
        case Updated() =>
          clearErrors()
          route(PoolsPage)
        case _ => log(s"Pool -> update handler failed: $event")

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
            }
          },
          btn("Cleanings").amend {
            onClick --> { _ =>
              log("Cleanings menu item onClick")
            }
          },
          btn("Chemicals").amend {
            onClick --> { _ =>
              log("Chemicals menu item onClick")
            }
          }
        ),
        dropdown(
          btn("Expenses"),
          btn("Supplies").amend {
            onClick --> { _ =>
              log("Supplies menu item onClick")
            }
          },
          btn("Repairs").amend {
            onClick --> { _ =>
              log("Repairs menu item onClick")
            }
          }
        ),
        dropdown(
          btn("Hardware"),
          btn("Pumps").amend {
            onClick --> { _ =>
              log("Pumps menu item onClick")
            }
          },
          btn("Timers").amend {
            onClick --> { _ =>
              log("Timers menu item onClick")
            }
          },
          btn("Heaters").amend {
            onClick --> { _ =>
              log("Heaters menu item onClick")
            }
          }
        ),
        dropdown(
          btn("Aesthetics"),
          btn("Surfaces").amend {
            onClick --> { _ =>
              log("Surfaces menu item onClick")
            }
          },
          btn("Decks").amend {
            onClick --> { _ =>
              log("Decks menu item onClick")
            }
          },
        )
      ),
      div(
        hdr("Pool"),
        err(errorBus),
        lbl("Name"),
        txt.amend {
          value <-- model.selectedEntityVar.signal.map(_.name)
          onInput.mapToValue.filter(_.nonEmpty) --> { name =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(name = name) )
          }
          onKeyUp.mapToValue --> { name =>
            if name.isName then clear(nameErrorBus) else emit(nameErrorBus, nameError)
          }
        },
        err(nameErrorBus),
        lbl("Built"),
        year.amend {
          value <-- model.selectedEntityVar.signal.map(_.built.toString)
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { built =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(built = built) )
          }
          onKeyUp.mapToValue.map(_.toInt) --> { built =>
            if built.isGreaterThan1899 then clear(builtErrorBus) else emit(builtErrorBus, builtError)
          }
        },
        err(builtErrorBus),
        lbl("Volume"),
        txt.amend {
          value <-- model.selectedEntityVar.signal.map(_.volume.toString)
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { volume =>
            model.updateSelectedEntity( model.selectedEntityVar.now().copy(volume = volume) )
          }
          onKeyUp.mapToValue.map(_.toInt) --> { volume =>
            if volume.isGreaterThan999 then clear(volumeErrorBus) else emit(volumeErrorBus, volumeError)
          }
        },
        err(volumeErrorBus)
      ),
      cbar(
        btn("Add").amend {
          disabled <-- model.selectedEntityVar.signal.map { pool => pool.id.isGreaterThanZero }
          onClick --> { _ =>
            log(s"Pool -> Add onClick")
            val command = AddPool(accountVar.now().license, model.selectedEntityVar.now())
            call(command, addHandler)

          }
        },
        btn("Update").amend {
          disabled <-- model.selectedEntityVar.signal.map { pool => pool.id.isZero }
          onClick --> { _ =>
            log(s"Pool -> Update onClick")
            val command = UpdatePool(accountVar.now().license, model.selectedEntityVar.now())
            call(command, updateHandler)
          }
        }
      )
    )