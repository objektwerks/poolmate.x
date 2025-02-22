Pool
----
>Web pool management app using Cask, uPickle, Scalikejdbc, ScalaJs, Laminar, Waypoint, W3.CSS, Scaffeine, JoddMail, Postgresql, Snowpack and Scala 3.

Todo
----
1. Test UI.

Charts
------
>Chart.js, via scalablytyped converter, does not work all too well at this time.
>Alternatively charts could be generated as png files via JFreeChart on the server.

Install
-------
1. brew install postgresql
2. brew install node
3. npm install jsdom ( must install **locally** )
4. graalvm ( https://www.graalvm.org/docs/getting-started/ )
5. npm install ( in project root directory )
>See **package.json** for installable dependencies.

Build
-----
1. npm install ( only when package.json changes )
2. sbt clean compile fastLinkJS
>See **js/target/public** directory.

Test
----
1. sbt clean test fastLinkJS

Dev
---
1. sbt jvm/run ( new session, curl -v http://localhost:7272/now )
2. sbt ( new session )
3. ~ js/fastLinkJS
4. npx snowpack dev ( new session )
>Edits are reflected in the **fastLinkJS** and **snowpack** sessions.
>See **snowpack.config.json** and [Snowpack Config](https://www.snowpack.dev/reference/configuration) for configurable options.

Package Server
--------------
>See sbt-native-packager ( www.scala-sbt.org/sbt-native-packager/formats/universal.html )
1. sbt clean test fullLinkJS
2. sbt jvm/universal:packageZipTarball | sbt 'show graalvm-native-image:packageBin'
>**Optionally** execute Graalvm image: ./jvm/target/graalvm-native-image/scala.graalvm

Package Client
--------------
1. sbt clean test fullLinkJS
2. npx snowpack build ( see **build** directory )

Client
------
* Now
* Command => Event

Server
------
1. Now: /now
2. Api: /command

Use Cases
---------
1. **clean** artifacts
2. **measure** water
3. **add** chemicals
4. **set** timer | heater
5. **expense** supplies, repairs, pumps, timers, heaters, surfaces, decks

Account
-------
* Register( emailAddress ) => Registered( account )
* Login( emailAddress, pin ) => LoggedIn( account )
* Deactivate( license ) => Deactivated( account )
* Reactivate( license ) => Reactivated( account )

View(Menu) ! Action -> Page
---------------------------
1. Root
   * Root(Login, Register) ! Login | Register -> Login
   * Login ! Login -> App
2. App
   * App(Account, Pools)
   * Account(App) ! Deactivate, Reactivate
   * Pools(App) ! N -> Pool(Pools, Hardware, Maintenance, Expenses) ! AU
3. Maintenance **, ***
   * Measurements(Pool) ! N -> Measurement(Measurements) ! AU
   * Cleanings(Pool) ! N -> Cleaning(Cleanings) ! AU
   * Chemicals(Pool) ! N -> Chemical(Chemicals) ! AU
4. Expenses **, ***
   * Supplies(Pool) ! N -> Supply(Supplies) ! AU
   * Repairs(Pool) ! N -> Repair(Repairs) ! AU
5. Hardware **
   * Pumps(Pool) ! N -> Pump(Pumps) ! AU
   * Timers(Pool) ! N -> Timer(Timers) ! AU
     * Timer ! N -> TimerSettings(Timer) ! N -> TimerSetting ! AU
   * Heaters(Pool) ! N -> Heater(Heaters) ! AU -> Heaters
     * Heater ! N -> HeaterSettings(Heater) ! N -> HeaterSetting ! AU
6. Aesthetics **
   * Surfaces(Pool) ! N -> Surface(Surfaces) ! AU
   * Decks(Pool) ! N -> Deck(Surfaces) ! AU

** Actions:
* New = N
* Add, Update = AU
* Refresh = R ( all list views have a refresh button)
  
*** Charts:
* measurements, cleanings, chemicals
* supplies, repairs

Model
-----
1. Model -> Model[E <: Entity](entitiesVar: Var[Seq[E]], selectedEntityVar: Var[E], emptyEntity: E)

Entity Model
------------
* Pool 1..n ---> 1 Account **
* Pool 1 ---> 1..n Surface, Deck, Pump, Timer, TimerSetting, Heater, HeaterSetting, Measurement, Cleaning, Chemical, Supply, Repair
* Email 1..n ---> 1 Account **
* Fault
* UoM ( unit of measure )
>** Account contains a globally unique license.

Object Model
------------
* Router 1 ---> 1 Dispatcher, Store
* Service 1 ---> 1 Store
* Authorizer 1 ---> 1 Service
* Dispatcher 1 ---> 1 Authorizer, Validator, Service, EmailSender
* Scheduler 1 ---> 1 EmailProcesor 1 ---> 1 Store
* Server 1 ---> 1 Router
* Client

Sequence
--------
1. Client --- Command ---> Server
2. Server --- Command ---> Router
3. Router --- Command ---> Dispatcher
4. Dispatcher --- Command ---> Authorizer, Validator, Service, EmailSender
5. Authorizer, Validator, Service, EmailSender --- Event ---> Dispatcher
6. Dispatcher --- Event ---> Router
7. Router --- Event ---> Server
8. Server --- Event ---> Client
9. Scheduler ---> EmailProcessor

Measurements
------------
>Measured in ppm ( parts per million ).

| Measurement                       | Range     | Good        | Ideal |
|-----------------------------------|-----------|-------------|-------|
| total chlorine (tc = fc + cc)     | 0 - 10    | 1 - 5       | 3     |
| free chlorine (fc)                | 0 - 10    | 1 - 5       | 3     |
| combinded chlorine (cc = tc - fc) | 0.0 - 0.5 | 0.0 - 0.2   | 0.0   |
| ph                                | 6.2 - 8.4 | 7.2 - 7.6   | 7.4   |
| calcium hardness                  | 0 - 1000  | 250 - 500   | 375   |
| total alkalinity                  | 0 - 240   | 80 - 120    | 100   |
| cyanuric acid                     | 0 - 300   | 30 - 100    | 50    |
| total bromine                     | 0 - 20    | 2 - 10      | 5     |
| salt                              | 0 - 3600  | 2700 - 3400 | 3200  |
| temperature                       | 50 - 100  | 75 - 85     | 82    |

** Units of Measure - oz, gl, lb

Chemicals
---------
* Liquids measured in: gallons ( gl ) and liters ( l ).
* Granules measured in: pounds ( lbs ) and kilograms ( kg ).
1. LiquidChlorine ( gl/l )
2. Trichlor ( tablet )
3. Dichlor ( lbs/kg )
4. CalciumHypochlorite ( lbs/kg )
5. Stabilizer ( lbs/kg )
6. Algaecide ( gl/l )
7. MuriaticAcid ( gl/l )
8. Salt ( lbs/kg )

Products
--------
1. Chlorine for pool.
2. Chlorine tablets for pool filtration system.
3. Pool Shock

Solutions
---------
>Suggested solutions to chemical imbalances.
1. high ph - Sodium Bisulfate
2. low ph - Sodium Carbonate, Soda Ash
3. high alkalinity - Muriatic Acid, Sodium Bisulfate
4. low alkalinity - Sodium Bicarbonate, Baking Soda
5. calcium hardness - Calcium Chloride
6. low chlorine - Chlorine Tablets, Granules, Liquid
7. algae - Algaecide, Shock
8. stains - Stain Identification Kit, Stain Remover

Descriptions
------------
* cleanings, measurements

Images
------
* add, edit, chart

Charts
------
1. measurements - line chart ( x = date, y = chemical )
2. cleanings - line chart ( x = date, y = month )
3. chemicals - bar chart ( x = date, y = amount, c = chemical )
4. supplies - bar chart ( x = date, y = cost, c = item )
5. repairs - line chart ( x = date, y = cost )

Date
----
1. Format: yyyy-MM-dd
2. String: 1999-01-01, 1999-12-16
3. Int: 19990101, 19991216

Time
----
1. Format: HH:mm
2. String: 01:01, 19:14
3. Int: 101, 1914

Postgresql
----------
1. conf:
    1. on osx intel: /usr/local/var/postgres/postgresql.conf : listen_addresses = ‘localhost’, port = 5432
    2. on osx m1: /opt/homebrew/var/postgres/postgresql.conf : listen_addresses = ‘localhost’, port = 5432
2. build.sbt:
    1. IntegrationTest / javaOptions += "-Dquill.binds.log=true"
3. run:
    1. brew services start postgresql
4. logs:
    1. on osx intel: /usr/local/var/log/postgres.log
    2. on m1: /opt/homebrew/var/log/postgres.log

Database
--------
>Example database url: postgresql://localhost:5432/poolmate?user=mycomputername&password=poolmate"
1. psql postgres
2. CREATE DATABASE poolmate OWNER [your computer name];
3. GRANT ALL PRIVILEGES ON DATABASE poolmate TO [your computer name];
4. \l
5. \q
6. psql poolmate
7. \i ddl.sql
8. \q

DDL
---
>Alternatively run: psql -d poolmate -f ddl.sql
1. psql poolmate
2. \i ddl.sql
3. \q

Drop
----
1. psql postgres
2. drop database poolmate;
3. \q

Config
------
>See these files:
1. jvm/src/main/resoures/server.conf
2. jvm/src/test/resources/test.server.conf

Cache
-----
>See jvm/Store.cache

Cors Handler
------------
* See poolmate.CorsHandler and poolmate.Server
* Also see https://github.com/Download/undertow-cors-filter

Logs
----
>See logs at /target/
1. jvm.log
2. test.jvm.log
3. test.shared.log

Environment
-----------
>The following environment variables must be defined:
```
export POOLMATE_HOST="127.0.0.1"
export POOLMATE_PORT=7272

export POOLMATE_POSTGRESQL_URL="jdbc:postgresql://localhost:5432/dbname"
export POOLMATE_POSTGRESQL_USER="user"
export POOLMATE_POSTGRESQL_PASSWORD="password"
export POOLMATE_POSTGRESQL_POOL_INITIAL_SIZE=9
export POOLMATE_POSTGRESQL_POOL_MAX_SIZE=32
export POOLMATE_POSTGRESQL_POOL_CONNECTION_TIMEOUT_MILLIS=30000

export POOLMATE_EMAIL_HOST="mail.host.com"
export POOLMATE_EMAIL_ADDRESS="email@address.com"
export POOLMATE_EMAIL_PASSWORD="password"
```

Resources
---------
* [Cask](https://com-lihaoyi.github.io/cask/index.html)
* [uPickle](https://com-lihaoyi.github.io/upickle/)
* [Requests](https://github.com/com-lihaoyi/requests-scala)
* [ScalikeJdbc](http://scalikejdbc.org)
* [H2](https://h2database.com/html/main.html)
* [Scala-Java-Time](https://github.com/cquiroz/scala-java-time)
* [Scaffeine](https://github.com/blemale/scaffeine)
* [Packager](https://www.scala-sbt.org/sbt-native-packager/formats/graalvm-native-image.html)
* [Gaalvm](https://www.graalvm.org/docs/introduction/)
* [Snowpack](https://www.snowpack.dev/)

License
-------
>Copyright (c) [2023, 2024, 2025] [Objektwerks]

>Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    * http://www.apache.org/licenses/LICENSE-2.0

>Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
