package poolmate

import Validator.*

final class Dispatcher(authorizer: Authorizer,
                       service: Service,
                       emailSender: EmailSender):
  def dispatch(command: Command): Event =
    authorizer.authorize(command) match
      case unauthorized: Unauthorized => unauthorized
      case _ => 
        if command.isValid then handle(command)
        else Fault(s"Invalid command: $command")

  private def handle(command: Command): Event =
    command match
      case register: Register =>
        emailSender.send(register).fold(_ => Fault(s"Invalid email address: ${register.emailAddress}"), registering => registering)
      case login: Login =>
        service.login(login.emailAddress, login.pin).fold(throwable => Fault(throwable), account => LoggedIn(account))
      
      case deactivate: Deactivate =>
        service.deactivate(deactivate.license).fold(throwable => Fault(throwable), account => Deactivated(account))
      case reactivate: Reactivate =>
        service.reactivate(reactivate.license).fold(throwable => Fault(throwable), account => Reactivated(account))

      case list: ListPools =>
        service.listPools(list.license).fold(throwable => Fault(throwable), entities => PoolsListed(entities))
      case add: AddPool =>
        service.addPool(add.pool).fold(throwable => Fault(throwable), entity => PoolAdded(entity))
      case update: UpdatePool =>
        service.updatePool(update.pool).fold(throwable => Fault(throwable), _ => Updated(update.pool.id))

      case list: ListSurfaces =>
        service.listSurfaces(list.poolId).fold(throwable => Fault(throwable), entities => SurfacesListed(entities))
      case add: AddSurface =>
        service.addSurface(add.surface).fold(throwable => Fault(throwable), entity => SurfaceAdded(entity))
      case update: UpdateSurface =>
        service.updateSurface(update.surface).fold(throwable => Fault(throwable), _ => Updated(update.surface.id))

      case list: ListDecks =>
        service.listDecks(list.poolId).fold(throwable => Fault(throwable), entities => DecksListed(entities))
      case add: AddDeck =>
        service.addDeck(add.deck).fold(throwable => Fault(throwable), entity => DeckAdded(entity))
      case update: UpdateDeck =>
        service.updateDeck(update.deck).fold(throwable => Fault(throwable), _ => Updated(update.deck.id))

      case list: ListPumps =>
        service.listPumps(list.poolId).fold(throwable => Fault(throwable), entities => PumpsListed(entities))
      case add: AddPump =>
        service.addPump(add.pump).fold(throwable => Fault(throwable), entity => PumpAdded(entity))
      case update: UpdatePump =>
        service.updatePump(update.pump).fold(throwable => Fault(throwable), _ => Updated(update.pump.id))

      case list: ListTimers =>
        service.listTimers(list.poolId).fold(throwable => Fault(throwable), entities => TimersListed(entities))
      case add: AddTimer =>
        service.addTimer(add.timer).fold(throwable => Fault(throwable), entity => TimerAdded(entity))
      case update: UpdateTimer =>
        service.updateTimer(update.timer).fold(throwable => Fault(throwable), _ => Updated(update.timer.id))

      case list: ListTimerSettings =>
        service.listTimerSettings(list.timerId).fold(throwable => Fault(throwable), entities => TimerSettingsListed(entities))
      case add: AddTimerSetting =>
        service.addTimerSetting(add.timerSetting).fold(throwable => Fault(throwable), entity => TimerSettingAdded(entity))
      case update: UpdateTimerSetting =>
        service.updateTimerSetting(update.timerSetting).fold(throwable => Fault(throwable), _ => Updated(update.timerSetting.id))

      case list: ListHeaters =>
        service.listHeaters(list.poolId).fold(throwable => Fault(throwable), entities => HeatersListed(entities))
      case add: AddHeater =>
        service.addHeater(add.heater).fold(throwable => Fault(throwable), entity => HeaterAdded(entity))
      case update: UpdateHeater =>
        service.updateHeater(update.heater).fold(throwable => Fault(throwable), _ => Updated(update.heater.id))

      case list: ListHeaterSettings =>
        service.listHeaterSettings(list.heaterId).fold(throwable => Fault(throwable), entities => HeaterSettingsListed(entities))
      case add: AddHeaterSetting =>
        service.addHeaterSetting(add.heaterSetting).fold(throwable => Fault(throwable), entity => HeaterSettingAdded(entity))
      case update: UpdateHeaterSetting =>
        service.updateHeaterSetting(update.heaterSetting).fold(throwable => Fault(throwable), _ => Updated(update.heaterSetting.id))

      case list: ListMeasurements =>
        service.listMeasurements(list.poolId).fold(throwable => Fault(throwable), entities => MeasurementsListed(entities))
      case add: AddMeasurement =>
        service.addMeasurement(add.measurement).fold(throwable => Fault(throwable), id => MeasurementAdded(add.measurement.copy(id = id)))
      case update: UpdateMeasurement =>
        service.updateMeasurement(update.measurement).fold(throwable => Fault(throwable), _ => Updated(update.measurement.id))

      case list: ListCleanings =>
        service.listCleanings(list.poolId).fold(throwable => Fault(throwable), entities => CleaningsListed(entities))
      case add: AddCleaning =>
        service.addCleaning(add.cleaning).fold(throwable => Fault(throwable), id => CleaningAdded(add.cleaning.copy(id = id)))
      case update: UpdateCleaning =>
        service.updateCleaning(update.cleaning).fold(throwable => Fault(throwable), _ => Updated(update.cleaning.id))

      case list: ListChemicals =>
        service.listChemicals(list.poolId).fold(throwable => Fault(throwable), entities => ChemicalsListed(entities))
      case add: AddChemical =>
        service.addChemical(add.chemical).fold(throwable => Fault(throwable), id => ChemicalAdded(add.chemical.copy(id = id)))
      case update: UpdateChemical =>
        service.updateChemical(update.chemical).fold(throwable => Fault(throwable), _ => Updated(update.chemical.id))

      case list: ListSupplies =>
        service.listSupplies(list.poolId).fold(throwable => Fault(throwable), entities => SuppliesListed(entities))
      case add: AddSupply =>
        service.addSupply(add.supply).fold(throwable => Fault(throwable), entity => SupplyAdded(entity))
      case update: UpdateSupply =>
        service.updateSupply(update.supply).fold(throwable => Fault(throwable), _ => Updated(update.supply.id))

      case list: ListRepairs =>
        service.listRepairs(list.poolId).fold(throwable => Fault(throwable), entities => RepairsListed(entities))
      case add: AddRepair =>
        service.addRepair(add.repair).fold(throwable => Fault(throwable), entity => RepairAdded(entity))
      case update: UpdateRepair =>
        service.updateRepair(update.repair).fold(throwable => Fault(throwable), _ => Updated(update.repair.id))