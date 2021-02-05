import sbt.Keys.libraryDependencies

lazy val scalaSettings = Seq(
  scalaVersion := "2.12.13",
  scalacOptions += "-deprecation",
  scalacOptions += "-unchecked",
  scalacOptions += "-feature",
  scalacOptions += "-target:jvm-1.8",
  crossScalaVersions := Seq("2.12.13", "2.13.4"),
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.8" % "test"
  )
)

lazy val javaSettings = Seq(
  crossPaths := false,
  autoScalaLibrary := false,
  Compile / packageDoc / publishArtifact := false,
  crossScalaVersions := Seq("2.12.13") // it's not really used; it's just about turning-off the crosscompilation
)

lazy val Versions = new {
  val dropwizard = "4.1.17"
  val typesafeConfig = "1.4.1"
  val grpc = "1.35.0"
  val slf4j = "1.7.30"
  val assertj = "3.12.2"
}

lazy val commonSettings = Seq(
  sonatypeProfileName := "com.avast",
  organization := "com.avast.metrics",
  organizationName := "Avast",
  organizationHomepage := Some(url("https://avast.com")),
  homepage := Some(url("https://github.com/avast/metrics")),
  description := "Library for application monitoring",
  licenses ++= Seq("MIT" -> url(s"https://github.com/avast/metrics/blob/${version.value}/LICENSE")),
  developers := List(Developer("jakubjanecek", "Jakub Janecek", "jakub.janecek@avast.com", url("https://www.avast.com"))),
  publishArtifact in Test := false,
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  libraryDependencies ++= Seq(
    "org.mockito" % "mockito-all" % "1.10.19" % "test",
    "junit" % "junit" % "4.12" % "test",
    "com.novocode" % "junit-interface" % "0.11" % "test", // Required by sbt to execute JUnit tests
    "ch.qos.logback" % "logback-classic" % "1.2.3" % "test",
    "javax.annotation" % "javax.annotation-api" % "1.3.2" % "test" // for compatibility with JDK >8
  ),
  testOptions += Tests.Argument(TestFrameworks.JUnit)
)

lazy val root = (project in file("."))
  .settings(
    name := "metrics",
    commonSettings,
    publish := {},
    publishLocal := {},
    crossScalaVersions := Nil
  )
  .aggregate(api, scalaApi, core, dropwizardCommon, jmx, jmxAvast, graphite, filter, formatting, statsd, grpc)

lazy val api = (project in file("api")).settings(
  commonSettings,
  javaSettings,
  name := "metrics-api"
)

lazy val scalaApi = (project in file("scala-api"))
  .settings(
    commonSettings,
    scalaSettings,
    name := "metrics-scala"
  )
  .dependsOn(api, jmx % "test")

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    javaSettings,
    name := "metrics-core",
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-api" % Versions.slf4j,
      "org.assertj" % "assertj-core" % Versions.assertj % "test"
    )
  )
  .dependsOn(api)

lazy val dropwizardCommon = (project in file("dropwizard-common"))
  .settings(
    commonSettings,
    javaSettings,
    name := "metrics-dropwizard-common",
    libraryDependencies ++= Seq(
      "io.dropwizard.metrics" % "metrics-core" % Versions.dropwizard,
      "io.dropwizard.metrics" % "metrics-jmx" % Versions.dropwizard,
      "org.slf4j" % "slf4j-api" % Versions.slf4j
    )
  )
  .dependsOn(core)

lazy val jmx = (project in file("jmx"))
  .settings(
    commonSettings,
    javaSettings,
    name := "metrics-jmx"
  )
  .dependsOn(dropwizardCommon)

lazy val jmxAvast = (project in file("jmx-avast"))
  .settings(
    commonSettings,
    javaSettings,
    name := "metrics-jmx-avast"
  )
  .dependsOn(jmx)

lazy val graphite = (project in file("graphite"))
  .settings(
    commonSettings,
    javaSettings,
    name := "metrics-graphite",
    libraryDependencies ++= Seq(
      "io.dropwizard.metrics" % "metrics-graphite" % Versions.dropwizard
    )
  )
  .dependsOn(dropwizardCommon)

lazy val filter = (project in file("filter"))
  .settings(
    commonSettings,
    javaSettings,
    name := "metrics-filter",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % Versions.typesafeConfig
    )
  )
  .dependsOn(dropwizardCommon)

lazy val grpc = (project in file("grpc"))
  .settings(
    commonSettings,
    javaSettings,
    name := "metrics-grpc",
    libraryDependencies ++= Seq(
      "io.grpc" % "grpc-api" % Versions.grpc,
      "io.grpc" % "grpc-protobuf" % Versions.grpc % "test",
      "io.grpc" % "grpc-stub" % Versions.grpc % "test",
      "io.grpc" % "grpc-services" % Versions.grpc % "test",
      "com.google.protobuf" % "protobuf-java" % "3.14.0" % "test"
    )
  )
  .dependsOn(core)

lazy val formatting = (project in file("formatting"))
  .settings(
    commonSettings,
    javaSettings,
    name := "metrics-formatting",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % Versions.typesafeConfig
    )
  )
  .dependsOn(dropwizardCommon, filter)

lazy val statsd = (project in file("statsd"))
  .settings(
    commonSettings,
    javaSettings,
    name := "metrics-statsd",
    libraryDependencies ++= Seq(
      "com.datadoghq" % "java-dogstatsd-client" % "2.11.0",
      "org.slf4j" % "slf4j-api" % Versions.slf4j
    )
  )
  .dependsOn(core, filter)
