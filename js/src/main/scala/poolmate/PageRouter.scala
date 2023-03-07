package poolmate

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.*

import upickle.default.*

import Page.given
import Serializer.given

object PageRouter:
  val poolsRoute = Route.static(PoolsPage, root / "app" / "pools" / endOfSegments)
  val poolRoute = Route[PoolPage, Long](
    encode = page => page.id,
    decode = arg => PoolPage(id = arg),
    pattern = root / "app" / "pools" / segment[Long] / endOfSegments
  )

  val surfacesRoute = Route.static(SurfacesPage, root / "app" / "pools" / "pool" / "surfaces" / endOfSegments)
  val surfaceRoute = Route[SurfacePage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => SurfacePage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "surfaces" / segment[Long] / endOfSegments
  )

  val decksRoute = Route.static(DecksPage, root / "app" / "pools" / "pool" / "decks" / endOfSegments)
  val deckRoute = Route[DeckPage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => DeckPage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "decks" / segment[Long] / endOfSegments
  )

  val pumpsRoute = Route.static(PumpsPage, root / "app" / "pools" / "pool" / "pumps" / endOfSegments)
  val pumpRoute = Route[PumpPage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => PumpPage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "pumps" / segment[Long] / endOfSegments
  )

  val timersRoute = Route.static(TimersPage, root / "app" / "pools" / "pool" / "timers" / endOfSegments)
  val timerRoute = Route[TimerPage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => TimerPage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "timers" / segment[Long] / endOfSegments
  )

  val timerSettingRoute = Route[TimerSettingPage, (Long, Long, Long)](
    encode = page => (page.poolId, page.timerId, page.id),
    decode = (poolId, timerId, id) => TimerSettingPage(poolId, timerId, id),
    pattern = root / "app" / "pools" / segment[Long] / "timers" / segment[Long] / "timersettings" / segment[Long] / endOfSegments
  )

  val heaterRoute = Route[HeaterPage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => HeaterPage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "heaters" / segment[Long] / endOfSegments
  )

  val heaterSettingRoute = Route[HeaterSettingPage, (Long, Long, Long)](
    encode = page => (page.poolId, page.heaterId, page.id),
    decode = (poolId, heaterId, id) => HeaterSettingPage(poolId, heaterId, id),
    pattern = root / "app" / "pools" / segment[Long]/ "heaters" / segment[Long] /  "heatersettings" / segment[Long] / endOfSegments
  )

  val cleaningRoute = Route[CleaningPage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => CleaningPage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "cleanings" / segment[Long] / endOfSegments
  )

  val measurementRoute = Route[MeasurementPage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => MeasurementPage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "measurements" / segment[Long] / endOfSegments
  )

  val chemicalRoute = Route[ChemicalPage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => ChemicalPage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "chemicals" / segment[Long] / endOfSegments
  )

  val supplyRoute = Route[SupplyPage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => SupplyPage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "supplies" / segment[Long] / endOfSegments
  )

  val repairRoute = Route[RepairPage, (Long, Long)](
    encode = page => (page.poolId, page.id),
    decode = (poolId, id) => RepairPage(poolId, id),
    pattern = root / "app" / "pools" / segment[Long] / "repairs" / segment[Long] / endOfSegments
  )

  val routees = List(
    Route.static(HomePage, root / endOfSegments),

    Route.static(RegisterPage, root / "register" / endOfSegments),
    Route.static(LoginPage, root / "login" / endOfSegments),

    Route.static(AppPage, root / "app" / endOfSegments),
    Route.static(AccountPage, root / "app" / "account" / endOfSegments),

    poolsRoute,
    poolRoute,

    surfacesRoute,
    surfaceRoute,

    decksRoute,
    deckRoute,

    pumpsRoute,
    pumpRoute,

    timersRoute,
    timerRoute,

    Route.static(TimerSettingsPage, root / "app" / "pools" / "pool" / "timers" / "timer" / "timersettings" / endOfSegments),
    timerSettingRoute,
    Route.static(HeatersPage, root / "app" / "pools" / "pool" / "heaters" / endOfSegments),
    heaterRoute,
    Route.static(HeaterSettingsPage, root / "app" / "pools" / "pool" / "heaters" / "heater" / "heatersettings" / endOfSegments),
    heaterSettingRoute,
    Route.static(CleaningsPage, root / "app" / "pools" / "pool" / "cleanings" / endOfSegments),
    cleaningRoute,
    Route.static(MeasurementsPage, root / "app" / "pools" / "pool" / "measurements" / endOfSegments),
    measurementRoute,
    Route.static(ChemicalsPage, root / "app" / "pools" / "pool" / "chemicals" / endOfSegments),
    chemicalRoute,
    Route.static(SuppliesPage, root / "app" / "pools" / "pool" / "supplies" / endOfSegments),
    supplyRoute,
    Route.static(RepairsPage, root / "app" / "pools" / "pool" / "repairs" / endOfSegments),
    repairRoute
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
    .collectStatic(PoolsPage) { PoolsView(Model.pools, Model.license) }
    .collect[PoolPage] { page => PoolView(Model.pools.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(SurfacesPage) { SurfacesView(Model.pools.selectedEntityVar.now().id, Model.surfaces, Model.license) }
    .collect[SurfacePage] { page => SurfaceView(Model.surfaces.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(DecksPage) { DecksView(Model.pools.selectedEntityVar.now().id, Model.decks, Model.license) }
    .collect[DeckPage] { page => DeckView(Model.decks.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(PumpsPage) { PumpsView(Model.pools.selectedEntityVar.now().id, Model.pumps, Model.license) }
    .collect[PumpPage] { page => PumpView(Model.pumps.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(TimersPage) { TimersView(Model.pools.selectedEntityVar.now().id, Model.timers, Model.license) }
    .collect[TimerPage] { page => TimerView(Model.timers.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(TimerSettingsPage) { TimerSettingsView(Model.pools.selectedEntityVar.now().id, Model.timersettings, Model.license) }
    .collect[TimerSettingPage] { page => TimerSettingView(Model.timers.selectedEntityVar.now().id, Model.timersettings.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(HeatersPage) { HeatersView(Model.pools.selectedEntityVar.now().id, Model.heaters, Model.license) }
    .collect[HeaterPage] { page => HeaterView(Model.heaters.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(HeaterSettingsPage) { HeaterSettingsView(Model.pools.selectedEntityVar.now().id, Model.heatersettings, Model.license) }
    .collect[HeaterSettingPage] { page => HeaterSettingView(Model.heaters.selectedEntityVar.now().id, Model.heatersettings.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(CleaningsPage) { CleaningsView(Model.pools.selectedEntityVar.now().id, Model.cleanings, Model.license) }
    .collect[CleaningPage] { page => CleaningView(Model.cleanings.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(MeasurementsPage) { MeasurementsView(Model.pools.selectedEntityVar.now().id, Model.measurements, Model.license) }
    .collect[MeasurementPage] { page => MeasurementView(Model.measurements.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(ChemicalsPage) { ChemicalsView(Model.pools.selectedEntityVar.now().id, Model.chemicals, Model.license) }
    .collect[ChemicalPage] { page => ChemicalView(Model.chemicals.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(SuppliesPage) { SuppliesView(Model.pools.selectedEntityVar.now().id, Model.supplies, Model.license) }
    .collect[SupplyPage] { page => SupplyView(Model.supplies.setSelectedEntityById(page.id), Model.license) }

    .collectStatic(RepairsPage) { RepairsView(Model.pools.selectedEntityVar.now().id, Model.repairs, Model.license) }
    .collect[RepairPage] { page => RepairView(Model.repairs.setSelectedEntityById(page.id), Model.license) }