package poolmate

import com.github.blemale.scaffeine.{Cache, Scaffeine}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

import java.time.LocalDate

import scalikejdbc.*
import scala.concurrent.duration.FiniteDuration

object Store:
  def cache(minSize: Int,
            maxSize: Int,
            expireAfter: FiniteDuration): Cache[String, String] =
    Scaffeine()
      .initialCapacity(minSize)
      .maximumSize(maxSize)
      .expireAfterWrite(expireAfter)
      .build[String, String]()

final class Store(conf: Config,
                  cache: Cache[String, String]) extends LazyLogging:
  private val url = conf.getString("db.url")
  private val user = conf.getString("db.user")
  private val password = conf.getString("db.password")
  private val initialSize = conf.getInt("db.initialSize")
  private val maxSize = conf.getInt("db.maxSize")
  private val connectionTimeoutMillis = conf.getLong("db.connectionTimeoutMillis")

  private val settings = ConnectionPoolSettings(
    initialSize = initialSize,
    maxSize = maxSize,
    connectionTimeoutMillis = connectionTimeoutMillis)

  ConnectionPool.singleton(url, user, password, settings)

  def listAccounts(): List[Account] =
    DB readOnly { implicit session =>
      sql"select * from account"
      .map(rs =>
        Account(
          rs.long("id"),
          rs.string("license"),
          rs.string("email_address"),
          rs.string("pin"),
          rs.int("activated"),
          rs.int("deactivated")
        )
      )
      .list()
    }

  def addAccount(account: Account): Account =
    val id = DB localTx { implicit session =>
      sql"""
          insert into account(license, email_address, pin, activated, deactivated) 
          values(${account.license}, ${account.emailAddress}, ${account.pin}, ${account.activated}, ${account.deactivated})
         """
      .update()
    }
    account.copy(id = id)

  def removeAccount(license: String): Unit =
    DB localTx { implicit session =>
      sql"delete account where license = $license"
      .update()
    }
    ()

  def listUnprocessedEmails: List[Email] =
    DB readOnly { implicit session =>
      sql"select * from email where processed = false"
      .map(rs => 
        Email(
          rs.string("id"),
          rs.string("license"),
          rs.string("address"),
          rs.int("date_sent"),
          rs.int("time_sent"),
          rs.boolean("processed"),
          rs.boolean("valid")
        )
      )
      .list()
    }

  def addEmail(email: Email): Unit =
    DB localTx { implicit session =>
      sql"""
          insert into email(id, license, address, date_sent, time_sent, processed, valid)
          values(${email.id}, ${email.license}, ${email.address}, ${email.dateSent},
          ${email.timeSent}, ${email.processed}, ${email.valid})
         """
      .update()
    }

  def processEmail(email: Email): Unit =
    DB localTx { implicit session =>
      sql"update email set processed = ${email.processed}, valid = ${email.valid} where id = ${email.id}"
      .update()
    }
    ()
    
  def login(email: String, pin: String): Option[Account] =
    DB readOnly { implicit session =>
      sql"select * from account where email_address = $email and pin = $pin"
      .map(rs =>
        Account(
          rs.long("id"),
          rs.string("license"),
          rs.string("email_address"),
          rs.string("pin"),
          rs.int("activated"),
          rs.int("deactivated")
        )
      )
      .single()
    }

  def isAuthorized(license: String): Boolean =
    cache.getIfPresent(license) match
      case Some(_) =>
        logger.debug(s"*** store cache get: $license")
        true
      case None =>
        val optionalLicense = DB readOnly { implicit session =>
          sql"select license from account where license = $license"
          .map(rs => rs.string("license"))
          .single()
        }
        if optionalLicense.isDefined then
          cache.put(license, license)
          logger.debug(s"*** store cache put: $license")
          true
        else false

  def deactivate(license: String): Option[Account] =
    DB localTx { implicit session =>
      val deactivated = sql"update account set deactivated = ${LocalDate.now.toEpochDay}, activated = 0 where license = $license"
                        .update()
      if deactivated > 0 then
        sql"select * from account where license = $license"
        .map(rs =>
          Account(
            rs.long("id"),
            rs.string("license"),
            rs.string("email_address"),
            rs.string("pin"),
            rs.int("activated"),
            rs.int("deactivated")
          )
        )
        .single()
      else None
    }

  def reactivate(license: String): Option[Account] =
    DB localTx { implicit session =>
      val activated = sql"update account set activated = ${LocalDate.now.toEpochDay}, deactivated = 0 where license = $license"
                      .update()
      if activated > 0 then
        sql"select * from account where license = $license"
        .map(rs =>
          Account(
            rs.long("id"),
            rs.string("license"),
            rs.string("email_address"),
            rs.string("pin"),
            rs.int("activated"),
            rs.int("deactivated")
          )
        )
        .single()
      else None
    }

  def listPools(license: String): List[Pool] =
    DB readOnly { implicit session =>
      sql"select * from pool where license = $license order by built desc"
      .map(rs =>
        Pool(
          rs.long("id"),
          rs.string("license"),
          rs.string("name"),
          rs.int("volume"),
          rs.string("unit"),
          rs.int("cost"),
          rs.int("built")
        )
      )
      .list()
    }

  def addPool(pool: Pool): Pool =
    val id = DB localTx { implicit session =>
      sql"""
          insert into pool(license, name, volume, unit, cost, built) 
          values(${pool.license}, ${pool.name}, ${pool.volume}, ${pool.unit}, ${pool.cost}, ${pool.built})
         """
      .updateAndReturnGeneratedKey()
    }
    pool.copy(id = id)
    
  def updatePool(pool: Pool): Unit =
    DB localTx { implicit session =>
      sql"""
          pdate pool set name = ${pool.name}, volume = ${pool.volume}, unit = ${pool.unit}, cost = ${pool.cost}, built = ${pool.built} 
          where id = ${pool.id}
         """
      .update()
    }
    ()

  def listSurfaces(): List[Surface] =
    DB readOnly { implicit session =>
      sql"select * from surface order by installed desc"
      .map(rs => Surface(rs.long("id"), rs.long("pool_id"), rs.string("kind"), rs.int("cost"), rs.int("installed")))
      .list()
    }

  def addSurface(surface: Surface): Surface =
    val id = DB localTx { implicit session =>
      sql"insert into surface(pool_id, kind, cost, installed) values(${surface.poolId}, ${surface.kind}, ${surface.cost}, ${surface.installed})"
      .updateAndReturnGeneratedKey()
    }
    surface.copy(id = id)

  def updateSurface(surface: Surface): Unit =
    DB localTx { implicit session =>
      sql"update surface set kind = ${surface.kind}, cost = ${surface.cost}, installed = ${surface.installed} where id = ${surface.id}"
      .update()
    }
    ()

  def listDecks(): List[Deck] =
    DB readOnly { implicit session =>
      sql"select * from deck order by installed desc"
      .map(rs => Deck(rs.long("id"), rs.long("pool_id"), rs.string("kind"), rs.int("cost"), rs.int("installed")))
      .list()
    }

  def addDeck(deck: Deck): Deck =
    val id = DB localTx { implicit session =>
      sql"insert into deck(pool_id, kind, cost, installed) values(${deck.poolId}, ${deck.kind}, ${deck.cost}, ${deck.installed})"
      .updateAndReturnGeneratedKey()
    }
    deck.copy(id = id)

  def updateDeck(deck: Deck): Unit =
    DB localTx { implicit session =>
      sql"update deck set kind = ${deck.kind}, cost = ${deck.cost}, installed = ${deck.installed} where id = ${deck.id}"
      .update()
    }
    ()

  def listPumps(): List[Pump] =
    DB readOnly { implicit session =>
      sql"select * from pump order by installed desc"
      .map(rs => Pump(rs.long("id"), rs.long("pool_id"), rs.string("model"), rs.int("cost"), rs.int("installed")))
      .list()
    }

  def addPump(pump: Pump): Pump =
    val id = DB localTx { implicit session =>
      sql"insert into pump(pool_id, model, cost, installed) values(${pump.poolId}, ${pump.model}, ${pump.cost}, ${pump.installed})"
      .updateAndReturnGeneratedKey()
    }
    pump.copy(id = id)  
  
  def updatePump(pump: Pump): Unit =
    DB localTx { implicit session =>
      sql"update pump set model = ${pump.model}, cost = ${pump.cost}, installed = ${pump.installed} where id = ${pump.id}"
      .update()
    }
    ()

  def listTimers(): List[Timer] =
    DB readOnly { implicit session =>
      sql"select * from timer order by installed desc"
      .map(rs => Timer(rs.long("id"), rs.long("pool_id"), rs.string("model"), rs.int("cost"), rs.int("installed")))
      .list()
    }

  def addTimer(timer: Timer): Timer =
    val id = DB localTx { implicit session =>
      sql"insert into timer(pool_id, model, cost, installed) values(${timer.poolId}, ${timer.model}, ${timer.cost}, ${timer.installed})"
      .updateAndReturnGeneratedKey()
    }
    timer.copy(id = id)
  
  def updateTimer(timer: Timer): Unit =
    DB localTx { implicit session =>
      sql"update timer set model = ${timer.model}, cost = ${timer.cost}, installed = ${timer.installed} where id = ${timer.id}"
      .update()
    }
    ()

  def listTimerSettings(): List[TimerSetting] =
    DB readOnly { implicit session =>
      sql"select * from timer_setting order by created desc"
      .map(rs => TimerSetting(rs.long("id"), rs.long("timer_id"), rs.int("created"), rs.int("time_on"), rs.int("time_off")))
      .list()
    }

  def addTimerSetting(timerSetting: TimerSetting): TimerSetting =
    val id = DB localTx { implicit session =>
      sql"""
          insert into timer_setting(timer_id, created, time_on, time_off) 
          alues(${timerSetting.timerId}, ${timerSetting.created}, ${timerSetting.timeOn}, ${timerSetting.timeOff})
         """
      .updateAndReturnGeneratedKey()
    }
    timerSetting.copy(id = id)

  def updateTimerSetting(timerSetting: TimerSetting): Unit =
    DB localTx { implicit session =>
      sql"""
          update timer_setting set created = ${timerSetting.created}, time_on = ${timerSetting.timeOn}, time_off = ${timerSetting.timeOff} 
          where id = ${timerSetting.id}
         """
      .update()
    }
    ()

  def listHeaters(): List[Heater] =
    DB readOnly { implicit session =>
      sql"select * from heater order by installed desc"
      .map(rs => Heater(rs.long("id"), rs.long("pool_id"), rs.int("installed"), rs.string("model"), rs.int("cost")))
      .list()
    }

  def addHeater(heater: Heater): Heater =
    val id = DB localTx { implicit session =>
      sql"insert into heater(pool_id, installed, model, cost) values(${heater.poolId}, ${heater.installed}, ${heater.model}, ${heater.cost})"
      .updateAndReturnGeneratedKey()
    }
    heater.copy(id = id)

  def updateHeater(heater: Heater): Unit =
    DB localTx { implicit session =>
      sql"update heater set installed = ${heater.installed}, model = ${heater.model}, cost = ${heater.cost} where id = ${heater.id}"
      .update()
    }
    ()

  def listHeaterSettings(): List[HeaterSetting] =
    DB readOnly { implicit session =>
      sql"select * from heater_setting order by date_on desc"
      .map(rs => HeaterSetting(rs.long("id"), rs.long("heater_id"), rs.int("temp"), rs.int("date_on"), rs.int("date_off")))
      .list()
    }

  def addHeaterSetting(heaterSetting: HeaterSetting): HeaterSetting =
    val id = DB localTx { implicit session =>
      sql"""
          insert into heater_setting(heater_id, temp, date_on, date_off) 
          values(${heaterSetting.heaterId}, ${heaterSetting.temp}, ${heaterSetting.dateOn}, ${heaterSetting.dateOff})
         """
      .updateAndReturnGeneratedKey()
    }
    heaterSetting.copy(id = id)

  def updateHeaterSetting(heaterSetting: HeaterSetting): Unit =
    DB localTx { implicit session =>
      sql"""
          update heater_setting set temp = ${heaterSetting.temp}, date_on = ${heaterSetting.dateOn}, date_off = ${heaterSetting.dateOff} 
          where id = ${heaterSetting.id}
         """
      .update()
    }
    ()

  def listMeasurements(poolId: Long): List[Measurement] = DB readOnly { implicit session =>
    sql"select * from measurement where pool_id = $poolId order by measured desc"
    .map(rs =>
      Measurement(
        rs.long("id"),
        rs.long("pool_id"),
        rs.int("total_chlorine"),
        rs.int("free_chlorine"),
        rs.double("combined_chlorine"),
        rs.double("ph"),
        rs.int("calcium_hardness"),
        rs.int("total_alkalinity"),
        rs.int("cyanuric_acid"),
        rs.int("total_bromine"),
        rs.int("salt"),
        rs.int("temperature"),
        rs.long("measured")
      )
    )
    .list()
  }

  def addMeasurement(measurement: Measurement): Long = DB localTx { implicit session =>
    sql"""
        insert into measurement(pool_id, total_chlorine, free_chlorine, combined_chlorine, ph, calcium_hardness,
        total_alkalinity, cyanuric_acid, total_bromine, salt, temperature, measured)
        values(${measurement.poolId}, ${measurement.totalChlorine}, ${measurement.freeChlorine}, ${measurement.combinedChlorine},
        ${measurement.ph}, ${measurement.calciumHardness}, ${measurement.totalAlkalinity}, ${measurement.cyanuricAcid},
        ${measurement.totalBromine}, ${measurement.salt}, ${measurement.temperature}, ${measurement.measured})
       """
    .updateAndReturnGeneratedKey()
  }

  def updateMeasurement(measurement: Measurement): Unit = DB localTx { implicit session =>
    sql"""
        update measurement set total_chlorine = ${measurement.totalChlorine}, free_chlorine = ${measurement.freeChlorine},
        combined_chlorine = ${measurement.combinedChlorine}, ph = ${measurement.ph}, calcium_hardness = ${measurement.calciumHardness},
        total_alkalinity = ${measurement.totalAlkalinity}, cyanuric_acid = ${measurement.cyanuricAcid},
        total_bromine = ${measurement.totalBromine}, salt = ${measurement.salt}, temperature = ${measurement.temperature},
        measured = ${measurement.measured}
        where id = ${measurement.id}
       """
    .update()
  }
  
  def listCleanings(poolId: Long): List[Cleaning] = DB readOnly { implicit session =>
    sql"select * from cleaning where pool_id = $poolId order by cleaned desc"
    .map(rs =>
      Cleaning(
        rs.long("id"),
        rs.long("pool_id"),
        rs.boolean("brush"),
        rs.boolean("net"),
        rs.boolean("skimmer_basket"),
        rs.boolean("pump_basket"),
        rs.boolean("pump_filter"),
        rs.boolean("vacuum"),
        rs.long("cleaned")
      )
    )
    .list()
  }

  def addCleaning(cleaning: Cleaning): Long = DB localTx { implicit session =>
    sql"""
        insert into cleaning(pool_id, brush, net, skimmer_basket, pump_basket, pump_filter, vacuum, cleaned)
        values(${cleaning.poolId}, ${cleaning.brush}, ${cleaning.net}, ${cleaning.skimmerBasket},
        ${cleaning.pumpBasket}, ${cleaning.pumpFilter}, ${cleaning.vacuum}, ${cleaning.cleaned})
       """
    .updateAndReturnGeneratedKey()
  }

  def updateCleaning(cleaning: Cleaning): Unit = DB localTx { implicit session =>
    sql"""
        update cleaning set brush = ${cleaning.brush}, net = ${cleaning.net}, skimmer_basket = ${cleaning.skimmerBasket},
        pump_basket = ${cleaning.pumpBasket}, pump_filter = ${cleaning.pumpFilter}, vacuum = ${cleaning.vacuum},
        cleaned = ${cleaning.cleaned} where id = ${cleaning.id}
       """
    .update()
  }

  def listChemicals(poolId: Long): List[Chemical] = DB readOnly { implicit session =>
    sql"select * from chemical where pool_id = $poolId order by added desc"
    .map(rs =>
      Chemical(
        rs.long("id"),
        rs.long("pool_id"),
        rs.string("chemical"),
        rs.double("amount"),
        rs.string("unit"),
        rs.long("added")
      )
    )
    .list()
  }

  def addChemical(chemical: Chemical): Long = DB localTx { implicit session =>
    sql"""
        insert into chemical(pool_id, chemical, amount, unit, added)
        values(${chemical.poolId}, ${chemical.chemical.toString}, ${chemical.amount}, ${chemical.unit.toString}, ${chemical.added})
       """
    .updateAndReturnGeneratedKey()
  }

  def updateChemical(chemical: Chemical): Unit = DB localTx { implicit session =>
    sql"""
        update chemical set chemical = ${chemical.chemical.toString}, amount = ${chemical.amount}, unit = ${chemical.unit.toString},
        added = ${chemical.added} where id = ${chemical.id}
       """
    .update()
  }

  def listSupplies(): List[Supply] =
    DB readOnly { implicit session =>
      sql"select * from supply order by purchased desc"
      .map(rs =>
        Supply(
          rs.long("id"),
          rs.long("pool_id"),
          rs.string("item"),
          rs.double("amount"),
          rs.string("unit"),
          rs.int("cost"),
          rs.long("purchased")
        )
      )
      .list()
    }

  def addSupply(supply: Supply): Supply =
    val id = DB localTx { implicit session =>
      sql"""
          insert into supply(pool_id, item, amount, unit, cost, purchased)
          values(${supply.poolId}, ${supply.item}, ${supply.amount}, ${supply.unit}, ${supply.cost}, ${supply.purchased})
         """
      .updateAndReturnGeneratedKey()
    }
    supply.copy(id = id)

  def updateSupply(supply: Supply): Unit =
    DB localTx { implicit session =>
      sql"""
          update supply set item = ${supply.item}, amount = ${supply.amount}, unit = ${supply.unit},
          cost = ${supply.cost}, purchased = ${supply.purchased} where id = ${supply.id}
         """
      .update()
    }
    ()

  def listRepairs(): List[Repair] =
    DB readOnly { implicit session =>
      sql"select * from repair order by repaired desc"
      .map(rs =>
        Repair(
          rs.long("id"),
          rs.long("pool_id"),
          rs.string("repair"),
          rs.int("cost"),
          rs.long("repaired")
        )
      )
      .list()
    }

  def addRepair(repair: Repair): Repair =
    val id = DB localTx { implicit session =>
      sql"""
          insert into repair(pool_id, repair, cost, repaired) 
          values(${repair.poolId}, ${repair.repair}, ${repair.cost}), ${repair.repaired}
         """
      .updateAndReturnGeneratedKey()
    }
    repair.copy(id = id)

  def updateRepair(repair: Repair): Unit =
    DB localTx { implicit session =>
      sql"update repair set repair = ${repair.repair}, cost = ${repair.cost}, repaired = ${repair.repaired} where id = ${repair.id}"
      .update()
    }
    ()

  def listFaults(): List[Fault] = DB readOnly { implicit session =>
    sql"select * from fault order by occurred desc"
    .map(rs =>
      Fault(
        rs.string("cause"),
        rs.string("occurred")
      )
    )
    .list()
  }

  def addFault(fault: Fault): Long = DB localTx { implicit session =>
    sql"insert into fault(cause, occurred) values(${fault.cause}, ${fault.occurred})"
    .updateAndReturnGeneratedKey()
  }