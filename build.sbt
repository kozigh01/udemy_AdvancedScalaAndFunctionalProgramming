ThisBuild / scalaVersion := "3.2.0"
ThisBuild / version := "1.0.0"
ThisBuild / organization := "com.mkozi"

lazy val section2 = (project in file("section2"))
lazy val section3 = (project in file("section3"))
lazy val section4 = (project in file("section4"))


lazy val root = (project in file("."))
  .aggregate(section2, section3, section4)
  .dependsOn(section4)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.14" % Test
)