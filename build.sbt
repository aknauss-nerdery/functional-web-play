import sbt._

name := "thing"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.5.6"

mainClass in (Compile, run) := Some("thing.Main")
