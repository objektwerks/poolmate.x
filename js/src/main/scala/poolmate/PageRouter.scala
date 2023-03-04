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

  val surfaceRoute = Route[SurfacePage, Long](
    encode = page => page.id,
    decode = arg => SurfacePage(id = arg),
    pattern = root / "app" / "pool" / "surfaces" / segment[Long] / endOfSegments
  )

  val deckRoute = Route[DeckPage, Long](
    encode = page => page.id,
    decode = arg => DeckPage(id = arg),
    pattern = root / "app" / "pool" / "decks" / segment[Long] / endOfSegments
  )

  val pumpRoute = Route[PumpPage, Long](
    encode = page => page.id,
    decode = arg => PumpPage(id = arg),
    pattern = root / "app" / "pool" / "pumps" / segment[Long] / endOfSegments
  )

  val timerRoute = Route[TimerPage, Long](
    encode = page => page.id,
    decode = arg => TimerPage(id = arg),
    pattern = root / "app" / "pool" / "timers" / segment[Long] / endOfSegments
  )

  val timerSettingRoute = Route[TimerSettingPage, Long](
    encode = page => page.id,
    decode = arg => TimerSettingPage(id = arg),
    pattern = root / "app" / "pool" / "timersettings" / segment[Long] / endOfSegments
  )

  val heaterRoute = Route[HeaterPage, Long](
    encode = page => page.id,
    decode = arg => HeaterPage(id = arg),
    pattern = root / "app" / "pool" / "heaters" / segment[Long] / endOfSegments
  )

  val heaterSettingRoute = Route[HeaterSettingPage, Long](
    encode = page => page.id,
    decode = arg => HeaterSettingPage(id = arg),
    pattern = root / "app" / "pool" / "heatersettings" / segment[Long] / endOfSegments
  )

  val cleaningRoute = Route[CleaningPage, Long](
    encode = page => page.id,
    decode = arg => CleaningPage(id = arg),
    pattern = root / "app" / "pool" / "cleanings" / segment[Long] / endOfSegments
  )

  val measurementRoute = Route[MeasurementPage, Long](
    encode = page => page.id,
    decode = arg => MeasurementPage(id = arg),
    pattern = root / "app" / "pool" / "measurements" / segment[Long] / endOfSegments
  )

  val chemicalRoute = Route[ChemicalPage, Long](
    encode = page => page.id,
    decode = arg => ChemicalPage(id = arg),
    pattern = root / "app" / "pool" / "chemicals" / segment[Long] / endOfSegments
  )

  val supplyRoute = Route[SupplyPage, Long](
    encode = page => page.id,
    decode = arg => SupplyPage(id = arg),
    pattern = root / "app" / "pool" / "supplies" / segment[Long] / endOfSegments
  )

  val repairRoute = Route[RepairPage, Long](
    encode = page => page.id,
    decode = arg => RepairPage(id = arg),
    pattern = root / "app" / "pool" / "repairs" / segment[Long] / endOfSegments
  )

  val routees = List(
    Route.static(HomePage, root / endOfSegments),
    Route.static(RegisterPage, root / "register" / endOfSegments),
    Route.static(LoginPage, root / "login" / endOfSegments),
    Route.static(AppPage, root / "app" / endOfSegments),
    Route.static(AccountPage, root / "app" / "account" / endOfSegments),
    Route.static(PoolsPage, root / "app" / "pools" / endOfSegments),
    poolRoute,
    Route.static(SurfacesPage, root / "app" / "surfaces" / endOfSegments),
    surfaceRoute,
    Route.static(DecksPage, root / "app" / "decks" / endOfSegments),
    deckRoute,
    Route.static(PumpsPage, root / "app" / "pumps" / endOfSegments),
    pumpRoute,
    Route.static(TimersPage, root / "app" / "timers" / endOfSegments),
    timerRoute,
    Route.static(TimerSettingsPage, root / "app" / "timersettings" / endOfSegments),
    timerSettingRoute,
    Route.static(HeatersPage, root / "app" / "heaters" / endOfSegments),
    heaterRoute,
    Route.static(CleaningsPage, root / "app" / "pool" / "cleanings" / endOfSegments),
    cleaningRoute,
    Route.static(MeasurementsPage, root / "app" / "pool" / "measurements" / endOfSegments),
    measurementRoute,
    Route.static(ChemicalsPage, root / "app" / "pool" / "chemicals" / endOfSegments),
    chemicalRoute
  )

  val router = new com.raquo.waypoint.Router[Page](
    routes = routees,
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
    .collectStatic(MeasurementsPage) { MeasurementsView(Model.pools.selectedEntityVar.now().id, Model.measurements, Model.license) }
    .collect[MeasurementPage] { page => MeasurementView(Model.measurements.setSelectedEntityById(page.id), Model.license) }
    .collectStatic(CleaningsPage) { CleaningsView(Model.cleanings.selectedEntityVar.now().id, Model.cleanings, Model.license) }
    .collect[CleaningPage] { page => CleaningView(Model.cleanings.setSelectedEntityById(page.id), Model.license) }
    .collectStatic(ChemicalsPage) { ChemicalsView(Model.pools.selectedEntityVar.now().id, Model.chemicals, Model.license) }
    .collect[ChemicalPage] { page => ChemicalView(Model.chemicals.setSelectedEntityById(page.id), Model.license) }