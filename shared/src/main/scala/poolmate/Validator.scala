package poolmate

object Validator:
  extension (command: Command)
    def isValid: Boolean =
      command match
        case register: Register => register.isValid
        case login: Login => login.isValid

        case deactivate: Deactivate => deactivate.isValid
        case reactivate: Reactivate => reactivate.isValid

        case list: ListPools => true
        case add: AddPool => add.pool.isValid
        case update: UpdatePool => update.pool.isValid

        case list: ListSurfaces => true
        case add: AddSurface => add.surface.isValid
        case update: UpdateSurface => update.surface.isValid

        case list: ListDecks => true
        case add: AddDeck => add.deck.isValid
        case update: UpdateDeck => update.deck.isValid

        case list: ListPumps => true
        case add: AddPump => add.pump.isValid
        case update: UpdatePump => update.pump.isValid

        case list: ListTimers => true
        case add: AddTimer => add.timer.isValid
        case update: UpdateTimer => update.timer.isValid

        case list: ListTimerSettings => true
        case add: AddTimerSetting => add.timerSetting.isValid
        case update: UpdateTimerSetting => update.timerSetting.isValid

        case list: ListHeaters => true
        case add: AddHeater => add.heater.isValid
        case update: UpdateHeater => update.heater.isValid

        case list: ListHeaterSettings => true
        case add: AddHeaterSetting => add.heaterSetting.isValid
        case update: UpdateHeaterSetting => update.heaterSetting.isValid

        case list: ListMeasurements => true
        case add: AddMeasurement => add.measurement.isValid
        case update: UpdateMeasurement => update.measurement.isValid

        case list: ListCleanings => true
        case add: AddCleaning => add.cleaning.isValid
        case update: UpdateCleaning => update.cleaning.isValid

        case list: ListChemicals => true
        case add: AddChemical => add.chemical.isValid
        case update: UpdateChemical => update.chemical.isValid

        case list: ListSupplies => true
        case add: AddSupply => add.supply.isValid
        case update: UpdateSupply => update.supply.isValid

        case list: ListRepairs => true
        case add: AddRepair => add.repair.isValid
        case update: UpdateRepair => update.repair.isValid

  extension (value: String)
    def isLicense: Boolean = if value.nonEmpty then value.length == 36 else false
    def isEmailAddress: Boolean = value.nonEmpty && value.length >= 3 && value.contains("@")
    def isPin: Boolean = value.length == 7
    def isName: Boolean = value.length >= 2

  extension (value: Int)
    def isGreaterThan1899 = value > 1899
    def isGreaterThan999 = value > 999

  extension (id: Long)
    def isZero: Boolean = id == 0
    def isGreaterThanZero: Boolean = id > 0

  extension (register: Register)
    def isValid: Boolean = register.emailAddress.isEmailAddress

  extension (login: Login)
    def isValid: Boolean = login.emailAddress.isEmailAddress && login.pin.isPin

  extension (deactivate: Deactivate)
    def isValid: Boolean = deactivate.license.isLicense

  extension (reactivate: Reactivate)
    def isValid: Boolean = reactivate.license.isLicense

  extension (account: Account)
    def isActivated: Boolean =
      account.id >= 0 &&
      account.license.isLicense &&
      account.emailAddress.isEmailAddress &&
      account.pin.isPin &&
      account.activated > 0 &&
      account.deactivated == 0
    def isDeactivated: Boolean =
      account.license.isLicense &&
      account.emailAddress.isEmailAddress &&
      account.pin.isPin &&
      account.activated == 0 &&
      account.deactivated > 0

  extension (pool: Pool) def isValid =
    pool.id >= 0 &&
    pool.license.isLicense &&
    pool.name.nonEmpty &&
    pool.built > 0 &&
    pool.volume >= 1000

  extension (surface: Surface)
    def isValid: Boolean =
      surface.id >= 0 &&
      surface.poolId > 0 &&
      surface.installed > 0 &&
      surface.kind.nonEmpty

  extension (deck: Deck)
    def isValid: Boolean =
      deck.id >= 0 &&
      deck.poolId > 0 &&
      deck.installed > 0 &&
      deck.kind.nonEmpty

  extension (pump: Pump)
    def isValid: Boolean =
      pump.id >= 0 &&
      pump.poolId > 0 &&
      pump.installed > 0 &&
      pump.model.nonEmpty

  extension (timer: Timer)
    def isValid: Boolean =
      timer.id >= 0 &&
      timer.poolId > 0 &&
      timer.installed > 0 &&
      timer.model.nonEmpty

  extension (timerSetting: TimerSetting)
    def isValid: Boolean =
      timerSetting.id >= 0 &&
      timerSetting.timerId > 0 &&
      timerSetting.created > 0 &&
      timerSetting.timeOn > 0 &&
      timerSetting.timeOff > 0 &&
      timerSetting.timeOff > timerSetting.timeOn

  extension (heater: Heater)
    def isValid: Boolean =
      heater.id >= 0 &&
      heater.poolId > 0 &&
      heater.installed > 0 &&
      heater.model.nonEmpty

  extension (heaterSetting: HeaterSetting)
    def isValid: Boolean =
      heaterSetting.id >= 0 &&
      heaterSetting.heaterId > 0 &&
      heaterSetting.temp > 0 &&
      heaterSetting.dateOn > 0 &&
      heaterSetting.dateOff >= 0

  extension (measurement: Measurement)
    def isValid: Boolean =
      import Measurement.*

      measurement.id >= 0 &&
      measurement.poolId > 0 &&
      totalChlorineRange.contains(measurement.totalChlorine) &&
      freeChlorineRange.contains(measurement.freeChlorine) &&
      combinedChlorineRange.contains(measurement.combinedChlorine) &&
      (measurement.ph >= 6.2 && measurement.ph <= 8.4) &&
      calciumHardnessRange.contains(measurement.calciumHardness) &&
      totalAlkalinityRange.contains(measurement.totalAlkalinity) &&
      cyanuricAcidRange.contains(measurement.cyanuricAcid) &&
      totalBromineRange.contains(measurement.totalBromine) &&
      saltRange.contains(measurement.salt) &&
      temperatureRange.contains(measurement.temperature) &&
      measurement.measured > 0

  extension (cleaning: Cleaning)
    def isValid: Boolean =
      cleaning.id >= 0 &&
      cleaning.poolId > 0 &&
      cleaning.cleaned > 0

  extension (chemical: Chemical)
    def isValid: Boolean =
      chemical.id >= 0 &&
      chemical.poolId > 0 &&
      chemical.chemical.nonEmpty &&
      chemical.amount > 0.00 &&
      chemical.unit.nonEmpty
      chemical.added > 0

  extension (supply: Supply)
    def isValid: Boolean =
      supply.id >= 0 &&
      supply.poolId > 0 &&
      supply.purchased > 0 &&
      supply.item.nonEmpty &&
      supply.amount > 0.00 &&
      supply.unit.nonEmpty &&
      supply.cost > 0.00

  extension (repair: Repair)
    def isValid: Boolean =
      repair.id >= 0 &&
      repair.poolId > 0 &&
      repair.repaired > 0 &&
      repair.repair.nonEmpty &&
      repair.cost > 0.00