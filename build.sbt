// See README.md for license details.

def scalacOptionsVersion(scalaVersion: String): Seq[String] = {
  Seq() ++ {
    // If we're building with Scala > 2.11, enable the compile option
    //  switch to support our anonymous Bundle definitions:
    //  https://github.com/scsala/bug/issues/10047
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor: Long)) if scalaMajor < 12 => Seq()
      case _ => Seq("-Xsource:2.11")
    }
  }
}

def javacOptionsVersion(scalaVersion: String): Seq[String] = {
  Seq() ++ {
    // Scala 2.12 requires Java 8. We continue to generate
    //  Java 7 compatible code for Scala 2.11
    //  for compatibility with old clients.
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor: Long)) if scalaMajor < 12 =>
        Seq("-source", "1.7", "-target", "1.7")
      case _ =>
        Seq("-source", "1.8", "-target", "1.8")
    }
  }
}

name := "chisel_version_test"

version := "3.3.3"

scalaVersion := "2.12.10"

crossScalaVersions := Seq("2.12.10", "2.11.12")

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)

/*val defaultVersions = Map(
  "chisel3" -> "3.5-SNAPSHOT",
  "firrtl" -> "1.5-SNAPSHOT",
  "firrtl-interpreter" -> "1.5-SNAPSHOT",
  "treadle" -> "1.5-SNAPSHOT"
)*/

val commonSettings = Seq(
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.12.12",
  crossScalaVersions := Seq("2.12.12", "2.11.12"),
  scalacOptions ++= scalacOptionsVersion(scalaVersion.value),
  javacOptions ++= javacOptionsVersion(scalaVersion.value),
  resolvers ++= Seq (
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases")
  )
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
//addCompilerPlugin("edu.berkeley.cs" %% "chisel3-plugin" % defaultVersions("chisel3") cross CrossVersion.full)

lazy val chisel = (project in file("chisel3"))
  .settings(commonSettings: _*)

lazy val chisel_testers = (project in file("chisel-testers"))
  .dependsOn(chisel)
  .settings(commonSettings: _*)

lazy val chisel_version_test = (project in file("."))
  .dependsOn(chisel, chisel_testers)
  .settings(commonSettings: _*)



