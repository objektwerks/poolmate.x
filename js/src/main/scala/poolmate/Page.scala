package poolmate

import com.raquo.laminar.api.L.*

import upickle.default.*

object Page:
  given pageRW: ReadWriter[Page] = ReadWriter.merge( entityPageRW )

  given poolPageRW: ReadWriter[PoolPage] = macroRW
  given cleaningPageRW: ReadWriter[CleaningPage] = macroRW
  given measurementPageRW: ReadWriter[MeasurementPage] = macroRW
  given chemicalPageRW: ReadWriter[ChemicalPage] = macroRW

  given entityPageRW: ReadWriter[EntityPage] = ReadWriter.merge(
    poolPageRW, cleaningPageRW, measurementPageRW, chemicalPageRW
  )

sealed trait Page:
  val title = "Poolmate"

case object HomePage extends Page
case object RegisterPage extends Page
case object LoginPage extends Page

case object AppPage extends Page
case object AccountPage extends Page
case object PoolsPage extends Page
case object SurfacesPage extends Page
case object DecksPage extends Page
case object CleaningsPage extends Page
case object MeasurementsPage extends Page
case object ChemicalsPage extends Page

sealed trait EntityPage extends Page:
  val id: Long

final case class PoolPage(id: Long = 0) extends EntityPage
final case class CleaningPage(id: Long = 0) extends EntityPage
final case class MeasurementPage(id: Long = 0) extends EntityPage
final case class ChemicalPage(id: Long = 0) extends EntityPage