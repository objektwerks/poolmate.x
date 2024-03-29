package poolmate

import java.time.{LocalDate, LocalTime}

enum UnitOfMeasure:
  case gl, l, lb, kg, tablet

object UnitOfMeasure:
  def toList: List[String] = UnitOfMeasure.values.map(uom => uom.toString).toList
  def toPoolList: List[String] = List( UnitOfMeasure.gl.toString, UnitOfMeasure.l.toString )
  def gallonsToLiters(gallons: Double): Double = gallons * 3.785
  def litersToGallons(liters: Double): Double = liters * 0.264
  def poundsToKilograms(pounds: Double): Double = pounds * 0.454
  def kilogramsToPounds(kilograms: Double): Double = kilograms * 2.205

enum TypeOfChemical(val display: String):
  case LiquidChlorine extends TypeOfChemical("Liquid Chlorine")
  case Trichlor extends TypeOfChemical("Trichlor")
  case Dichlor extends TypeOfChemical("Dichlor")
  case CalciumHypochlorite extends TypeOfChemical("Calcium Hypochlorite")
  case Stabilizer extends TypeOfChemical("Stabilizer")
  case Algaecide extends TypeOfChemical("Algaecide")
  case MuriaticAcid extends TypeOfChemical("Muriatic Acid")
  case Salt extends TypeOfChemical("Salt")

object TypeOfChemical:
  def toEnum(display: String): TypeOfChemical = TypeOfChemical.valueOf(display.filterNot(_.isWhitespace))
  def toList: List[String] = TypeOfChemical.values.map(toc => toc.display).toList

final case class Email(id: String,
                       license: String,
                       address: String,
                       dateSent: Long = Entity.currentEpochDay(),
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

  def currentEpochDay(): Long = LocalDate.now.toEpochDay
  def localDateOfLongToString(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).toString
  def localDateOfStringToLong(localDate: String): Long = LocalDate.parse(localDate).toEpochDay
  
  def localTimeOfLongToString(secondOfDay: Long): String = LocalTime.ofSecondOfDay(secondOfDay).toString
  def localTimeOfStringToInt(localTime: String): Int = LocalTime.parse(localTime).toSecondOfDay()

final case class Account(id: Long = 0,
                         license: String = "",
                         emailAddress: String = "",
                         pin: String = "",
                         activated: Long = Entity.currentEpochDay(),
                         deactivated: Long = 0) extends Entity:
  def display = emailAddress

final case class Pool(id: Long = 0,
                      license: String = "",
                      name: String = "",
                      volume: Int = 1000,
                      unit: String = UnitOfMeasure.gl.toString,
                      cost: Int = 0,
                      built: Long = 0) extends Entity:
  def display = name

final case class Surface(id: Long = 0,
                         poolId: Long = 0,
                         kind: String = "",
                         cost: Int = 0,
                         installed: Long = Entity.currentEpochDay()) extends Entity:
  def display = kind

final case class Deck(id: Long = 0,
                      poolId: Long = 0,
                      kind: String = "",
                      cost: Int = 0,
                      installed: Long = Entity.currentEpochDay()) extends Entity:
  def display = kind

final case class Pump(id: Long = 0,
                      poolId: Long = 0,
                      model: String = "",
                      cost: Int = 0,
                      installed: Long = Entity.currentEpochDay()) extends Entity:
  def display = model

final case class Timer(id: Long = 0,
                       poolId: Long = 0,
                       model: String = "",
                       cost: Int = 0,
                       installed: Long = Entity.currentEpochDay()) extends Entity:
  def display = model

final case class TimerSetting(id: Long = 0,
                              timerId: Long = 0,
                              created: Long = Entity.currentEpochDay(),
                              timeOn: Int = 0,
                              timeOff: Int = 0) extends Entity:
  def display = s"${LocalDate.ofEpochDay(created).toString}: $timeOn - $timeOff"

final case class Heater(id: Long = 0,
                        poolId: Long = 0,
                        model: String = "",
                        cost: Int = 0,
                        installed: Long = Entity.currentEpochDay()) extends Entity:
  def display = model

final case class HeaterSetting(id: Long = 0,
                               heaterId: Long = 0,
                               temp: Int = 0,
                               dateOn: Long = Entity.currentEpochDay(),
                               dateOff: Long = 0) extends Entity:
  def display = s"${LocalDate.ofEpochDay(dateOn).toString}: $temp"

object Measurement:
  val totalChlorineRange = Range(1, 5).inclusive
  val freeChlorineRange = Range(1, 5).inclusive
  val combinedChlorineRange = Set(0.0, 0.1, 0.2, 0.3, 0.4, 0.5)
  val phRange = Set(6.2, 6.3, 6.4, 6.5, 6.6, 6.7, 6.8, 6.9, 7.0, 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 7.7, 7.8, 7.9, 8.0, 8.1, 8.2, 8.3, 8.4)
  val calciumHardnessRange = Range(250, 500).inclusive
  val totalAlkalinityRange = Range(80, 120).inclusive
  val cyanuricAcidRange = Range(30, 100).inclusive
  val totalBromineRange = Range(2, 10).inclusive
  val saltRange = Range(2700, 3400).inclusive
  val temperatureRange = Range(50, 100).inclusive

final case class Measurement(id: Long = 0,
                             poolId: Long = 0,
                             totalChlorine: Int = 3,
                             freeChlorine: Int = 3,
                             combinedChlorine: Double = 0.0,
                             ph: Double = 7.4,
                             calciumHardness: Int = 375,
                             totalAlkalinity: Int = 100,
                             cyanuricAcid: Int = 50,
                             totalBromine: Int = 5,
                             salt: Int = 3200,
                             temperature: Int = 85,
                             measured: Long = Entity.currentEpochDay()) extends Entity:
  def display = LocalDate.ofEpochDay(measured).toString

final case class Cleaning(id: Long = 0,
                          poolId: Long = 0,
                          brush: Boolean = true,
                          net: Boolean = true,
                          skimmerBasket: Boolean = true,
                          pumpBasket: Boolean = false,
                          pumpFilter: Boolean = false,
                          vacuum: Boolean = false,
                          cleaned: Long = Entity.currentEpochDay()) extends Entity:
  def display = LocalDate.ofEpochDay(cleaned).toString

final case class Chemical(id: Long = 0,
                          poolId: Long = 0,
                          chemical: String = TypeOfChemical.LiquidChlorine.toString,
                          amount: Double = 1.0, 
                          unit: String = UnitOfMeasure.gl.toString,
                          added: Long = Entity.currentEpochDay()) extends Entity:
  def display = s"${LocalDate.ofEpochDay(added).toString}: $chemical"

final case class Supply(id: Long = 0,
                        poolId: Long = 0,                        
                        item: String = "",
                        amount: Double = 0.0,
                        unit: String = UnitOfMeasure.gl.toString,
                        cost: Int = 0,
                        purchased: Long = Entity.currentEpochDay()) extends Entity:
  def display = s"${LocalDate.ofEpochDay(purchased).toString}: $item"

final case class Repair(id: Long = 0,
                        poolId: Long = 0,
                        repair: String = "",
                        cost: Int = 0,
                        repaired: Long = Entity.currentEpochDay()) extends Entity:
  def display = s"${LocalDate.ofEpochDay(repaired).toString}: $repair"