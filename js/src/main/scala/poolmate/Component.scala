package poolmate

import com.raquo.laminar.api.L.*

import java.time.LocalDate

object Component:
  private val inputCss = "w3-input w3-hover-light-gray w3-text-indigo"
  private val currentYear = LocalDate.now.getYear

  def bar(elms: HtmlElement*): Div =
    div(cls("w3-bar"), elms)

  def cbar(elms: HtmlElement*): Div =
    div(cls("w3-bar w3-margin-top w3-center"), elms)

  def btn(text: String): Button =
    button(cls("w3-button w3-round-xxlarge w3-light-grey w3-text-indigo"), text)

  def rbtn(text: String): Button =
    button(cls("w3-button w3-round-xxlarge w3-light-grey w3-text-indigo w3-right"), text)
  
  def dropdown(header: Button, buttons: Button*): Div =
    div(cls("w3-dropdown-hover"), header, div(cls("w3-dropdown-content w3-bar-block w3-card-4"), buttons))

  def checkbox: Input =
    input(cls("w3-input"), tpe("checkbox"))

  def lbl(text: String): Label =
    label(cls("w3-left-align w3-text-indigo"), text)

  def info(text: String): Div =
    div(cls("w3-border-white w3-text-indigo"), b(text))

  def txt: Input =
    input(cls(inputCss), required(true))

  def rotxt: Input =
    input(cls(inputCss), readOnly(true))

  def email: Input =
    input(cls(inputCss), typ("email"), minLength(3), required(true))

  def pin: Input =
    input(cls(inputCss), typ("text"), minLength(6), maxLength(6), required(true))

  def year: Input =
    input(
      cls(inputCss), typ("number"), pattern("\\d*"),
      stepAttr("1"), minAttr("1900"), maxAttr(currentYear.toString),
      required(true)
    )

  def date: Input =
    input(cls(inputCss), tpe("date"), required(true))
 
  def time: Input =
    input(cls(inputCss), tpe("time"), required(true))

  def int: Input =
    input(cls(inputCss), typ("number"), pattern("\\d*"), stepAttr("1"), required(true))

  def dbl: Input =
    input(cls(inputCss), typ("number"), pattern("[0-9]+([.,][0-9]+)?"), stepAttr("0.01"), required(true))

  def hdr(text: String): HtmlElement =
    h5(cls("w3-light-grey w3-text-indigo"), text)

  def err(errBus: EventBus[String]): Div =
    div(cls("w3-border-white w3-text-red"), child.text <-- errBus.events)

  def msg(noteBus: EventBus[String]): Div =
    div(cls("w3-border-white w3-text-indigo"), child.text <-- noteBus.events)

  def listbox(items: List[String]): Select =
    select(cls("w3-select w3-text-indigo"),
      children <-- Var(items.map(item => option(item))).signal
    )

  def listview(liSignal: Signal[List[LI]]): Div =
    div(cls("w3-container"), ul(cls("w3-ul w3-hoverable"), overflow("overflow: auto;"), children <-- liSignal))

  def item(strSignal: Signal[String]): LI =
    li(cls("w3-text-indigo w3-display-container"), child.text <-- strSignal)

  def split[E <: Entity](entities: Var[List[E]], toEntityPage: Long => EntityPage): Signal[List[LI]] =
    entities.signal.split(_.id)( (id, _, entitySignal) =>
      item( entitySignal.map(_.display) ).amend {
        onClick --> { _ =>
          entities.now().find(_.id == id).foreach { entity =>
            PageRouter.router.pushState(toEntityPage(id))
          }
        }
      }
    )

  def grid(labelElements: List[(String, HtmlElement)],
           leftColWidth: Int = 50,
           rightColWidth: Int = 50): Div =
    div(cls("w3-container"), styleAttr("padding: 6px"),
      labelElements.map { (label, element) =>
        div( cls("w3-row"),
          div( cls("w3-col"), styleAttr(s"width:$leftColWidth%"), lbl(label) ),
          div( cls("w3-col"), styleAttr(s"width:$rightColWidth%"), element )
        )
      }
    )

  def dashboard: HtmlElement = Dashboard()