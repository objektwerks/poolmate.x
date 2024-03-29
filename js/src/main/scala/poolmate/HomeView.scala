package poolmate

import com.raquo.laminar.api.L.*

import Component.*

object HomeView extends View:
  def apply(): HtmlElement =
    div(
      bar(
        btn("Login").amend {
          onClick --> { _ =>
            log("Root -> Login menu item onClick")
            route(LoginPage)
          }
        },
        rbtn("Register").amend {
          onClick --> { _ =>
            log("Root -> Register menu item onClick")
            route(RegisterPage)
          }
        }          
      )
    )