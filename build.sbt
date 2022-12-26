ThisBuild / scalaVersion := "3.2.0"
ThisBuild / version := "1.0.0"
ThisBuild / organization := "com.mkozi"

lazy val section2 = (project in file("section2"))


lazy val root = (project in file("."))
  .aggregate(
    section2
  )

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.14" % Test
)