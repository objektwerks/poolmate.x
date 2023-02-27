package poolmate

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Entity.given

object Model:
  val emailAddressVar = Var("")
  val pinVar = Var("")
  val accountVar = Var(Account())
  val pools = Model[Pool](Var(List.empty[Pool]), Var(Pool()), Pool(), poolOrdering)
  val surfaces = Model[Surface](Var(List.empty[Surface]), Var(Surface()), Surface(), surfaceOrdering)
  val decks = Model[Deck](Var(List.empty[Deck]), Var(Deck()), Deck(), deckOrdering)
  val pumps = Model[Pump](Var(List.empty[Pump]), Var(Pump()), Pump(), pumpOrdering)
  val timers = Model[Timer](Var(List.empty[Timer]), Var(Timer()), Timer(), timerOrdering)
  val timersettings = Model[TimerSetting](Var(List.empty[TimerSetting]), Var(TimerSetting()), TimerSetting(), timerSettingOrdering)
  val heaters = Model[Heater](Var(List.empty[Heater]), Var(Heater()), Heater(), heaterOrdering)
  val heatersettings = Model[HeaterSetting](Var(List.empty[HeaterSetting]), Var(HeaterSetting()), HeaterSetting(), heaterSettingOrdering)
  val measurements = Model[Measurement](Var(List.empty[Measurement]), Var(Measurement()), Measurement(), measurementOrdering)
  val cleanings = Model[Cleaning](Var(List.empty[Cleaning]), Var(Cleaning()), Cleaning(), cleaningOrdering)
  val chemicals = Model[Chemical](Var(List.empty[Chemical]), Var(Chemical()), Chemical(), chemicalOrdering)
  val supplies = Model[Supply](Var(List.empty[Supply]), Var(Supply()), Supply(), supplyOrdering)
  val repairs = Model[Repair](Var(List.empty[Repair]), Var(Repair()), Repair(), repairOrdering)

final case class Model[E <: Entity](entitiesVar: Var[List[E]],
                                    selectedEntityVar: Var[E],
                                    emptyEntity: E,
                                    ordering: Ordering[E]):
  given owner: Owner = new Owner {}
  entitiesVar.signal.foreach(entities => log(s"model entities -> ${entities.toString}"))
  selectedEntityVar.signal.foreach(entity => log(s"model selected entity -> ${entity.toString}"))

  def setEntities(entities: List[E]): Unit = entitiesVar.set(entities)

  def setSelectedEntityById(id: Long): Model[E] =
    selectedEntityVar.set(entitiesVar.now().find(_.id == id).getOrElse(emptyEntity))
    this

  def sort(): Unit =
    val sortedEntities = entitiesVar.now().sorted[E](ordering)
    entitiesVar.set(sortedEntities)

  def addEntity(entity: E): Unit =
    val updatedEntities = entity +: entitiesVar.now()
    entitiesVar.set(updatedEntities)

  def updateSelectedEntity(updatedSelectedEntity: E): Unit =
    entitiesVar.update { entities =>
      entities.map { entity =>
        if entity.id == updatedSelectedEntity.id then
          selectedEntityVar.set(updatedSelectedEntity)
          updatedSelectedEntity
        else entity
      }
    }