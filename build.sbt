name := "poolmate.x"

lazy val caskVersion = "0.9.4"
lazy val laminarVersion = "17.1.0"
lazy val waypointVersion = "8.0.1"
lazy val upickleVersion = "4.0.1"
lazy val postgresqlVersion = "42.7.4"
lazy val scalaJavaTimeVersion = "2.5.0"
lazy val scalaTestVersion = "3.2.19"

lazy val common = Defaults.coreDefaultSettings ++ Seq(
  organization := "objektwerks",
  version := "0.13-SNAPSHOT",
  scalaVersion := "3.5.1",
  scalacOptions ++= Seq(
    "-Wunused:all"
  )
)

lazy val poolmate: Project = project.in(file("."))
  .aggregate(sharedJs, sharedJvm, js, jvm)
  .settings(common)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % upickleVersion,
      "io.github.cquiroz" %% "scala-java-time" % scalaJavaTimeVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    )
  )

lazy val sharedJs = shared.js
lazy val sharedJvm = shared.jvm
lazy val public = "public"

lazy val js = (project in file("js"))
  .dependsOn(sharedJs)
  .enablePlugins(ScalaJSPlugin, ScalablyTypedConverterExternalNpmPlugin)
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % laminarVersion,
      "com.raquo" %%% "waypoint" % waypointVersion,
      "com.lihaoyi" %%% "upickle" % upickleVersion,
      "io.github.cquiroz" %%% "scala-java-time" % scalaJavaTimeVersion
    ),
    useYarn := true,
    externalNpm := {
      poolmate.base.getAbsoluteFile()
    },
    Compile / fastLinkJS / scalaJSLinkerOutputDirectory := target.value / public,
    Compile / fullLinkJS / scalaJSLinkerOutputDirectory := target.value / public
  )

lazy val jvm = (project in file("jvm"))
  .dependsOn(sharedJvm)
  .enablePlugins(JavaServerAppPackaging)
  .settings(common)
  .settings(
    reStart / mainClass := Some("poolmate.Server"),
    libraryDependencies ++= {
      Seq(
        "com.lihaoyi" %% "cask" % caskVersion,
        "com.lihaoyi" %% "upickle" % upickleVersion,
        "org.scalikejdbc" %% "scalikejdbc" % "4.3.1",
        "org.postgresql" % "postgresql" % postgresqlVersion,
        "io.github.cquiroz" %% "scala-java-time" % scalaJavaTimeVersion,
        "com.github.blemale" %% "scaffeine" % "5.3.0",
        "org.jodd" % "jodd-mail" % "7.1.0",
        "com.typesafe" % "config" % "1.4.3",
        "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
        "ch.qos.logback" % "logback-classic" % "1.5.8",
        "com.lihaoyi" %% "requests" % "0.8.0" % Test,
        "org.scalatest" %% "scalatest" % scalaTestVersion % Test
      )
    }
  )
