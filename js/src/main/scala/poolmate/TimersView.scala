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
        case _ => log(s"Timers -> handler failed: $event")

    div(
      
    )