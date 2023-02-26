package poolmate

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Entity.given

object Model:
  val emailAddressVar = Var("")
  val pinVar = Var("")
  val accountVar = Var(Account())
  val pools = Model[Pool](Var(List.empty[Pool]), Var(Pool()), Pool(), poolOrdering)

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