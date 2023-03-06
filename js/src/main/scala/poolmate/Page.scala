package poolmate

import com.raquo.laminar.api.L.*

import upickle.default.*

object Page:
  given pageRW: ReadWriter[Page] = ReadWriter.merge( entityPageRW )

  given poolPageRW: ReadWriter[PoolPage] = macroRW
  given surfacePageRW: ReadWriter[SurfacePage] = macroRW
  given deckPageRW: ReadWriter[DeckPage] = macroRW
  given pumpPageRW: ReadWriter[PumpPage] = macroRW
  given timerPageRW: ReadWriter[TimerPage] = macroRW
  given timerSettingPageRW: ReadWriter[TimerSettingPage] = macroRW
  given heaterPageRW: ReadWriter[HeaterPage] = macroRW

  given cleaningPageRW: ReadWriter[CleaningPage] = macroRW
  given measurementPageRW: ReadWriter[MeasurementPage] = macroRW
  given chemicalPageRW: ReadWriter[ChemicalPage] = macroRW
  given supplyPageRW: ReadWriter[SupplyPage] = macroRW
  given repairPageRW: ReadWriter[RepairPage] = macroRW
  given entityPageRW: ReadWriter[EntityPage] = ReadWriter.merge(
    poolPageRW, surfacePageRW, deckPageRW,
    cleaningPageRW, measurementPageRW, chemicalPageRW, supplyPageRW, repairPageRW
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
case object PumpsPage extends Page
case object TimersPage extends Page
case object TimerSettingsPage extends Page
case object HeatersPage extends Page
case object HeaterSettingsPage extends Page
case object CleaningsPage extends Page
case object MeasurementsPage extends Page
case object ChemicalsPage extends Page
case object SuppliesPage extends Page
case object RepairsPage extends Page

sealed trait EntityPage extends Page:
  val id: Long

final case class PoolPage(id: Long = 0) extends EntityPage
final case class SurfacePage(id: Long = 0) extends EntityPage
final case class DeckPage(id: Long = 0) extends EntityPage
final case class PumpPage(id: Long = 0) extends EntityPage
final case class TimerPage(id: Long = 0) extends EntityPage
final case class TimerSettingPage(id: Long = 0) extends EntityPage
final case class HeaterPage(id: Long = 0) extends EntityPage
final case class HeaterSettingPage(id: Long = 0) extends EntityPage
final case class CleaningPage(id: Long = 0) extends EntityPage
final case class MeasurementPage(id: Long = 0) extends EntityPage
final case class ChemicalPage(id: Long = 0) extends EntityPage
final case class SupplyPage(id: Long = 0) extends EntityPage
final case class RepairPage(id: Long = 0) extends EntityPage