package poolmate

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.*

import upickle.default.*

import Page.given
import Serializer.given

object PageRouter:
  val poolRoute = Route[PoolPage, Long](
    encode = page => page.id,
    decode = arg => PoolPage(id = arg),
    pattern = root / "app" / "pools" / segment[Long] / endOfSegments
  )

  val routes = List(
    Route.static(HomePage, root / endOfSegments),
    Route.static(RegisterPage, root / "register" / endOfSegments),
    Route.static(LoginPage, root / "login" / endOfSegments),
    Route.static(AppPage, root / "app" / endOfSegments),
    Route.static(AccountPage, root / "app" / "account" / endOfSegments),
    Route.static(PoolsPage, root / "app" / "pools" / endOfSegments),
    poolRoute
  )

  val router = new com.raquo.waypoint.Router[Page](
    routes = routes,
    serializePage = page => write(page)(pageRW),
    deserializePage = pageAsString => read(pageAsString)(pageRW),
    getPageTitle = _.title,
  )(
    popStateEvents = L.windowEvents(_.onPopState),
    owner = L.unsafeWindowOwner
  )

  val splitter = SplitRender[Page, HtmlElement](router.currentPageSignal)
    .collectStatic(HomePage) { RootView() }
    .collectStatic(RegisterPage) { RegisterView(Model.emailAddressVar, Model.pinVar, Model.accountVar) }
    .collectStatic(LoginPage) { LoginView(Model.emailAddressVar, Model.pinVar, Model.accountVar) }
    .collectStatic(AppPage) { AppView(Model.accountVar) }
    .collectStatic(AccountPage) { AccountView(Model.accountVar) }
    .collectStatic(PoolsPage) { PoolsView(Model.pools, Model.accountVar) }
    .collect[PoolPage] { page => PoolView(Model.pools.setSelectedEntityById(page.id), Model.accountVar) }