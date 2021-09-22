name := "FinalProjectScala"

version := "0.1"

scalaVersion := "2.13.6"

enablePlugins(FlywayPlugin)

flywayUrl := "jdbc:postgresql://localhost:5432/CapitalBikeShare"
flywayUser := "postgres"
flywayPassword := "qwerty123"
flywayLocations += "db/migration"
flywayBaselineOnMigrate := true

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-json" % "3.4.3",
  "com.github.t3hnar" %% "scala-bcrypt" % "4.1",
  "org.postgresql" % "postgresql" % "42.2.23",
  "org.hsqldb" % "hsqldb" % "2.6.0",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.4.2",
  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.7",
  "com.typesafe" % "config" % "1.4.1",
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "3.0.3",
  "io.spray" %% "spray-json" % "1.3.6",
  "org.slf4j" % "slf4j-simple" % "1.7.32" % Test,
  "log4j" % "log4j" % "1.2.14",
  "org.typelevel" %% "cats-core" % "2.6.1",
  "org.apache.poi" % "poi-ooxml" % "4.1.2",
  "org.apache.poi" % "poi" % "4.1.2",

  "com.typesafe.akka" %% "akka-actor" % "2.6.16",
  "com.typesafe.akka" %% "akka-stream" % "2.6.16",

  "com.typesafe.akka" %% "akka-http" % "10.2.6",
  "com.typesafe.akka" %% "akka-http-core" % "10.2.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.6"
)