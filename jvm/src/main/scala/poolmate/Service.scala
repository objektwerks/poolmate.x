package poolmate

import scala.util.Try

final class Service(store: Store):
  def login(email: String, pin: String): Either[Throwable, Account] =
    store.login(email, pin) match
      case Some(account) => Right(account)
      case None => Left(IllegalArgumentException(s"Invalid email: $email and/or pin: $pin"))

  def deactivate(license: String): Either[Throwable, Account] =
    store.deactivate(license) match
      case Some(account) => Right(account)
      case None => Left(IllegalArgumentException(s"Invalid license: $license"))

  def reactivate(license: String): Either[Throwable, Account] =
    store.reactivate(license) match
      case Some(account) => Right(account)
      case None => Left(IllegalArgumentException(s"Invalid license: $license"))

  def authorize(license: String): Event =
    if store.isAuthorized(license) then Authorized(license)
    else Unauthorized(s"Invalid license: $license")
  
  def listPools(license: String): Either[Throwable, List[Pool]] = Try( store.listPools(license) ).toEither
  def addPool(pool: Pool): Either[Throwable, Pool] = Try( store.addPool(pool) ).toEither
  def updatePool(pool: Pool): Either[Throwable, Unit] = Try( store.updatePool(pool) ).toEither

  def listSurfaces(poolId: Long): Either[Throwable, List[Surface]] = Try( store.listSurfaces() ).toEither
  def addSurface(surface: Surface): Either[Throwable, Surface] = Try( store.addSurface(surface) ).toEither
  def updateSurface(surface: Surface): Either[Throwable, Unit] = Try( store.updateSurface(surface) ).toEither

  def listDecks(poolId: Long): Either[Throwable, List[Deck]] = Try( store.listDecks() ).toEither
  def addDeck(deck: Deck): Either[Throwable, Deck] = Try( store.addDeck(deck) ).toEither
  def updateDeck(deck: Deck): Either[Throwable, Unit] = Try( store.updateDeck(deck) ).toEither

  def listPumps(poolId: Long): Either[Throwable, List[Pump]] = Try( store.listPumps() ).toEither
  def addPump(pump: Pump): Either[Throwable, Pump] = Try( store.addPump(pump) ).toEither
  def updatePump(pump: Pump): Either[Throwable, Unit] = Try( store.updatePump(pump) ).toEither

  def listTimers(poolId: Long): Either[Throwable, List[Timer]] = Try( store.listTimers() ).toEither
  def addTimer(timer: Timer): Either[Throwable, Timer] = Try( store.addTimer(timer) ).toEither
  def updateTimer(timer: Timer): Either[Throwable, Unit] = Try( store.updateTimer(timer) ).toEither

  def listTimerSettings(timerId: Long): Either[Throwable, List[TimerSetting]] = Try( store.listTimerSettings() ).toEither
  def addTimerSetting(timerSetting: TimerSetting): Either[Throwable, TimerSetting] = Try( store.addTimerSetting(timerSetting) ).toEither
  def updateTimerSetting(timerSetting: TimerSetting): Either[Throwable, Unit] = Try( store.updateTimerSetting(timerSetting) ).toEither

  def listHeaters(poolId: Long): Either[Throwable, List[Heater]] = Try( store.listHeaters() ).toEither
  def addHeater(heater: Heater): Either[Throwable, Heater] = Try( store.addHeater(heater) ).toEither
  def updateHeater(heater: Heater): Either[Throwable, Unit] = Try( store.updateHeater(heater) ).toEither

  def listHeaterSettings(heaterId: Long): Either[Throwable, List[HeaterSetting]] = Try( store.listHeaterSettings() ).toEither
  def addHeaterSetting(heaterSetting: HeaterSetting): Either[Throwable, HeaterSetting] = Try( store.addHeaterSetting(heaterSetting) ).toEither
  def updateHeaterSetting(heaterSetting: HeaterSetting): Either[Throwable, Unit] = Try( store.updateHeaterSetting(heaterSetting) ).toEither

  def listMeasurements(poolId: Long): Either[Throwable, List[Measurement]] = Try( store.listMeasurements(poolId) ).toEither
  def addMeasurement(measurement: Measurement): Either[Throwable, Long] = Try( store.addMeasurement(measurement) ).toEither
  def updateMeasurement(measurement: Measurement): Either[Throwable, Unit] = Try( store.updateMeasurement(measurement) ).toEither

  def listCleanings(poolId: Long): Either[Throwable, List[Cleaning]] = Try( store.listCleanings(poolId) ).toEither
  def addCleaning(cleaning: Cleaning): Either[Throwable, Long] = Try( store.addCleaning(cleaning) ).toEither
  def updateCleaning(cleaning: Cleaning): Either[Throwable, Unit] = Try( store.updateCleaning(cleaning) ).toEither

  def listChemicals(poolId: Long): Either[Throwable, List[Chemical]] = Try( store.listChemicals(poolId) ).toEither
  def addChemical(chemical: Chemical): Either[Throwable, Long] = Try( store.addChemical(chemical) ).toEither
  def updateChemical(chemical: Chemical): Either[Throwable, Unit] = Try( store.updateChemical(chemical) ).toEither

  def listSupplies(poolId: Long): Either[Throwable, List[Supply]] = Try( store.listSupplies() ).toEither
  def addSupply(supply: Supply): Either[Throwable, Supply] = Try( store.addSupply(supply) ).toEither
  def updateSupply(supply: Supply): Either[Throwable, Unit] = Try( store.updateSupply(supply) ).toEither

  def listRepairs(poolId: Long): Either[Throwable, List[Repair]] = Try( store.listRepairs() ).toEither
  def addRepair(repair: Repair): Either[Throwable, Repair] = Try( store.addRepair(repair) ).toEither
  def updateRepair(repair: Repair): Either[Throwable, Unit] = Try( store.updateRepair(repair) ).toEither