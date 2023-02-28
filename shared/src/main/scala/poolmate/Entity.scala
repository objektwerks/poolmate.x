package poolmate

import java.time.{LocalDate, LocalTime}

enum UoM(val abrv: String):
  case ounce extends UoM("oz")
  case gallon extends UoM("gl")
  case pounds extends UoM("lb")

final case class Email(id: String,
                       license: String,
                       address: String,
                       dateSent: Long = LocalDate.now.toEpochDay,
                       timeSent: Int = LocalTime.now.toSecondOfDay,
                       processed: Boolean = false,
                       valid: Boolean = false)

sealed trait Entity:
  val id: Long
  def display: String

object Entity:
  given poolOrdering: Ordering[Pool] = Ordering.by[Pool, String](p => p.name).reverse
  given surfaceOrdering: Ordering[Surface] = Ordering.by[Surface, Long](s => s.installed).reverse
  given deckOrdering: Ordering[Deck] = Ordering.by[Deck, Long](d => d.installed).reverse
  given pumpOrdering: Ordering[Pump] = Ordering.by[Pump, Long](p => p.installed).reverse
  given timerOrdering: Ordering[Timer] = Ordering.by[Timer, Long](t => t.installed).reverse
  given timerSettingOrdering: Ordering[TimerSetting] = Ordering.by[TimerSetting, Long](ts => ts.created).reverse
  given heaterOrdering: Ordering[Heater] = Ordering.by[Heater, Long](t => t.installed).reverse
  given heaterSettingOrdering: Ordering[HeaterSetting] = Ordering.by[HeaterSetting, Long](ts => ts.dateOn).reverse
  given measurementOrdering: Ordering[Measurement] = Ordering.by[Measurement, Long](m => m.measured).reverse
  given cleaningOrdering: Ordering[Cleaning] = Ordering.by[Cleaning, Long](c => c.cleaned).reverse
  given chemicalOrdering: Ordering[Chemical] = Ordering.by[Chemical, Long](c => c.added).reverse
  given supplyOrdering: Ordering[Supply] = Ordering.by[Supply, Long](s => s.purchased).reverse
  given repairOrdering: Ordering[Repair] = Ordering.by[Repair, Long](r => r.repaired).reverse

final case class Account(id: Long = 0,
                         license: String = "",
                         emailAddress: String = "",
                         pin: String = "",
                         activated: Long = LocalDate.now.toEpochDay,
                         deactivated: Long = 0) extends Entity:
  def display = emailAddress

final case class Pool(id: Long = 0,
                      license: String = "",
                      name: String = "",
                      built: Long = 0,
                      volume: Int = 1000,
                      cost: Int = 0) extends Entity:
  def display = name

final case class Surface(id: Long = 0,
                         poolId: Long = 0,
                         installed: Long = 0,
                         kind: String = "",
                         cost: Int = 0) extends Entity:
  def display = kind

final case class Deck(id: Long = 0,
                      poolId: Long = 0,
                      installed: Long = 0,
                      kind: String = "",
                      cost: Int = 0) extends Entity:
  def display = kind

final case class Pump(id: Long = 0,
                      poolId: Long = 0,
                      installed: Long = 0,
                      model: String = "",
                      cost: Int = 0) extends Entity:
  def display = model

final case class Timer(id: Long = 0,
                       poolId: Long = 0,
                       installed: Long = 0,
                       model: String = "",
                       cost: Int = 0) extends Entity:
  def display = model

final case class TimerSetting(id: Long = 0,
                              timerId: Long = 0,
                              created: Long = 0,
                              timeOn: Int = 0,
                              timeOff: Int = 0) extends Entity:
  def display = s"$created: $timeOn - $timeOff"

final case class Heater(id: Long = 0,
                        poolId: Long = 0,
                        installed: Long = 0,
                        model: String = "",
                        cost: Int = 0) extends Entity:
  def display = installed.toString

final case class HeaterSetting(id: Long = 0,
                               heaterId: Long = 0,
                               temp: Int = 0,
                               dateOn: Long = 0,
                               dateOff: Long = 0) extends Entity:
  def display = s"$dateOn: $temp"

final case class Measurement(id: Long = 0,
                             poolId: Long = 0,
                             measured: Long = 0,
                             temp: Int = 85,
                             totalHardness: Int = 375,
                             totalChlorine: Int = 3,
                             totalBromine: Int = 5,
                             freeChlorine: Int = 3,
                             ph: Double = 7.4,
                             totalAlkalinity: Int = 100,
                             cyanuricAcid: Long = 50) extends Entity:
  def display = s"$measured: $ph ph"

object Measurement:
  val tempRange = 0 to 100
  val totalHardnessRange = 1 to 1000
  val totalChlorineRange = 0 to 10
  val totalBromineRange = 0 to 20
  val freeChlorineRange = 0 to 10
  val totalAlkalinityRange = 0 to 240
  val cyanuricAcidRange = 0 to 300

final case class Cleaning(id: Long = 0,
                          poolId: Long = 0,
                          cleaned: Long = 0,
                          brush: Boolean = true,
                          net: Boolean = true,
                          vacuum: Boolean = false,
                          skimmerBasket: Boolean = true,
                          pumpBasket: Boolean = false,
                          pumpFilter: Boolean = false,
                          deck: Boolean = false) extends Entity:
  def display = cleaned.toString

final case class Chemical(id: Long = 0,
                          poolId: Long = 0,
                          added: Long = 0,
                          chemical: String = "",
                          amount: Double = 0.0,
                          unit: String = "") extends Entity:
  def display = s"$added: $chemical"

final case class Supply(id: Long = 0,
                        poolId: Long = 0,
                        purchased: Long = 0,
                        item: String = "",
                        amount: Double = 0.0,
                        unit: String = "",
                        cost: Int = 0) extends Entity:
  def display = s"$purchased: $item"

final case class Repair(id: Long = 0,
                        poolId: Long = 0,
                        repaired: Long = 0,
                        repair: String = "",
                        cost: Int = 0) extends Entity:
  def display = s"$repaired: $repair"