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
  val timersetings = Model[TimerSetting](Var(List.empty[TimerSetting]), Var(TimerSetting()), TimerSetting(), timerSettingOrdering)
  val heaters = Model[Heater](Var(List.empty[Heater]), Var(Heater()), Heater(), heaterOrdering)
  val heatersetings = Model[HeaterSetting](Var(List.empty[HeaterSetting]), Var(HeaterSetting()), HeaterSetting(), heaterSettingOrdering)
  val measurementsModel = Model[Measurement](Var(List.empty[Measurement]), Var(Measurement()), Measurement(), measurementOrdering)

final case class Model[E <: Entity](entitiesVar: Var[List[E]],
                                    selectedEntityVar: Var[E],
                                    emptyEntity: E,
                                    ordering: Ordering[E]):
  given owner: Owner = new Owner {}
  entitiesVar.signal.foreach(entities => log(s"model entities -> ${entities.toString}"))
  selectedEntityVar.signal.foreach(entity => log(s"model selected entity -> ${entity.toString}"))

  def addEntity(entity: E): Unit = entitiesVar.update(_ :+ entity)

  def setEntities(entities: List[E]): Unit = entitiesVar.set(entities)

  def setSelectedEntityById(id: Long): Model[E] =
    selectedEntityVar.set(entitiesVar.now().find(_.id == id).getOrElse(emptyEntity))
    this

  def updateSelectedEntity(updatedSelectedEntity: E): Unit =
    entitiesVar.update { entities =>
      entities.map { entity =>
        if entity.id == updatedSelectedEntity.id then
          selectedEntityVar.set(updatedSelectedEntity)
          updatedSelectedEntity
        else entity
      }
    }

  def sort: Var[List[E]] =
    val sortedEntities = entitiesVar.now().sorted[E](ordering)
    entitiesVar.set(sortedEntities)
    entitiesVar