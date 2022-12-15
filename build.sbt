import Tests._

lazy val shiftMemRoot = Project("shiftMemRoot", file("."))
  .dependsOn(`rocket-dsp-utils`)
  .settings(
    chiselSettings,
    chiselTestSettings,
    commonSettings,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.+" % "test",
      "org.typelevel" %% "spire" % "0.16.2",
      "org.scalanlp" %% "breeze" % "1.1",
      "junit" % "junit" % "4.13" % "test",
      "org.scalacheck" %% "scalacheck" % "1.14.3" % "test",
  ))

lazy val commonSettings = Seq(
  organization := "edu.berkeley.cs",
  version := "1.6",
  scalaVersion := "2.12.10",
  assembly / test := {},
  assembly / assemblyMergeStrategy := { _ match {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case _ => MergeStrategy.first}},
  scalacOptions ++= Seq("-deprecation","-unchecked","-Xsource:2.11"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  //unmanagedBase := (shiftMemRoot / unmanagedBase).value,
  allDependencies := {
    // drop specific maven dependencies in subprojects in favor of Chipyard's version
    val dropDeps = Seq(
      ("edu.berkeley.cs", "firrtl"),
      ("edu.berkeley.cs", "chisel3"),
      ("edu.berkeley.cs", "rocketchip"),
      ("edu.berkeley.cs", "chisel-iotesters"),
      ("edu.berkeley.cs", "treadle"),
      ("edu.berkeley.cs", "firrtl-interpreter")))
    allDependencies.value.filterNot { dep =>
      dropDeps.contains((dep.organization, dep.name))
    }
  },
  exportJars := true,
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.mavenLocal))

val rocketChipDir = file("generators/rocket-chip")

/**
  * It has been a struggle for us to override settings in subprojects.
  * An example would be adding a dependency to rocketchip on midas's targetutils library,
  * or replacing dsptools's maven dependency on chisel with the local chisel project.
  *
  * This function works around this by specifying the project's root at src/ and overriding
  * scalaSource and resourceDirectory.
  */
def freshProject(name: String, dir: File): Project = {
  Project(id = name, base = dir / "src")
    .settings(
      Compile / scalaSource := baseDirectory.value / "main" / "scala",
      Compile / resourceDirectory := baseDirectory.value / "main" / "resources"
    )
}

val chiselVersion = "3.4.1"

lazy val chiselRef = ProjectRef(workspaceDirectory / "chisel3", "chisel")
lazy val chiselLib = "edu.berkeley.cs" %% "chisel3" % chiselVersion
lazy val chiselLibDeps = (chiselRef / Keys.libraryDependencies)
// While not built from source, *must* be in sync with the chisel3 git submodule
// Building from source requires extending sbt-sriracha or a similar plugin and
//   keeping scalaVersion in sync with chisel3 to the minor version
lazy val chiselPluginLib = "edu.berkeley.cs" % "chisel3-plugin" % chiselVersion cross CrossVersion.full


val firrtlVersion = "1.4.1"
lazy val firrtlRef = ProjectRef(workspaceDirectory / "firrtl", "firrtl")
lazy val firrtlLib = "edu.berkeley.cs" %% "firrtl" % firrtlVersion
val firrtlLibDeps = settingKey[Seq[sbt.librarymanagement.ModuleID]]("FIRRTL Library Dependencies sans antlr4")
Global / firrtlLibDeps := {
  // drop antlr4 compile dep. but keep antlr4-runtime dep. (compile needs the plugin to be setup)
  (firrtlRef / Keys.libraryDependencies).value.filterNot(_.name == "antlr4")
}


// Rocket-chip dependencies (subsumes making RC a RootProject)
lazy val hardfloat  = (project in rocketChipDir / "hardfloat")
  .sourceDependency(chiselRef, chiselLib)
  .settings(addCompilerPlugin(chiselPluginLib))
  .settings(libraryDependencies ++= chiselLibDeps.value)
  .dependsOn(midasTargetUtils)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.json4s" %% "json4s-jackson" % "3.6.1",
      "org.scalatest" %% "scalatest" % "3.2.0" % "test"
    )
  )

lazy val rocketMacros  = (project in rocketChipDir / "macros")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.json4s" %% "json4s-jackson" % "3.6.1",
      "org.scalatest" %% "scalatest" % "3.2.0" % "test"
    )
  )

lazy val rocketConfig = (project in rocketChipDir / "api-config-chipsalliance/build-rules/sbt")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.json4s" %% "json4s-jackson" % "3.6.1",
      "org.scalatest" %% "scalatest" % "3.2.0" % "test"
    )
  )

lazy val rocketchip = freshProject("rocketchip", rocketChipDir)
  .sourceDependency(chiselRef, chiselLib)
  .settings(addCompilerPlugin(chiselPluginLib))
  .settings(libraryDependencies ++= chiselLibDeps.value)
  .dependsOn(hardfloat, rocketMacros, rocketConfig)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.json4s" %% "json4s-jackson" % "3.6.1",
      "org.scalatest" %% "scalatest" % "3.2.0" % "test"
    )
  )
  .settings( // Settings for scalafix
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions += "-Ywarn-unused-import"
  )
lazy val rocketLibDeps = (rocketchip / Keys.libraryDependencies)

lazy val firrtl_interpreter = (project in file("tools/firrtl-interpreter"))
  .sourceDependency(firrtlRef, firrtlLib)
  .settings(commonSettings)
  .settings(libraryDependencies ++= (Global / firrtlLibDeps).value)
lazy val firrtlInterpreterLibDeps = (firrtl_interpreter / Keys.libraryDependencies)

lazy val treadle = (project in file("tools/treadle"))
  .sourceDependency(firrtlRef, firrtlLib)
  .settings(commonSettings)
  .settings(libraryDependencies ++= (Global / firrtlLibDeps).value)
lazy val treadleLibDeps = (treadle / Keys.libraryDependencies)

lazy val chisel_testers = (project in file("tools/chisel-testers"))
  .sourceDependency(chiselRef, chiselLib)
  .settings(addCompilerPlugin(chiselPluginLib))
  .settings(libraryDependencies ++= chiselLibDeps.value)
  .dependsOn(firrtl_interpreter, treadle)
  .settings(libraryDependencies ++= firrtlInterpreterLibDeps.value)
  .settings(libraryDependencies ++= treadleLibDeps.value)
  .settings(commonSettings)
lazy val chiselTestersLibDeps = (chisel_testers / Keys.libraryDependencies)


lazy val firesimDir = file("sims/firesim/sim/")
lazy val rocketLibDeps = (rocketchip / Keys.libraryDependencies)
lazy val midasTargetUtils = ProjectRef(firesimDir, "targetutils")

lazy val dsptools = freshProject("dsptools", file("./tools/dsptools"))
  .dependsOn(chisel_testers)
  .settings(libraryDependencies ++= chiselTestersLibDeps.value)
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "spire" % "0.16.2",
      "org.scalanlp" %% "breeze" % "1.1",
      "junit" % "junit" % "4.13" % "test",
      "org.scalatest" %% "scalatest" % "3.0.+" % "test",
      "org.scalacheck" %% "scalacheck" % "1.14.3" % "test",
  ))

lazy val `api-config-chipsalliance` = freshProject("api-config-chipsalliance", file("./tools/api-config-chipsalliance"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.+" % "test",
      "org.scalacheck" %% "scalacheck" % "1.14.3" % "test",
    ))


lazy val `rocket-dsp-utils` = freshProject("rocket-dsp-utils", file("./tools/rocket-dsp-utils"))
  .dependsOn(rocketchip, `api-config-chipsalliance`, dsptools)
  .settings(libraryDependencies ++= rocketLibDeps.value)
  .settings(commonSettings)


// Info: If the project is freshProject tests can not be run!!!
/*lazy val shiftMem = (project in file("./design")) //freshProject("shiftMem", file("./design")) //(project in file("./design"))
  .dependsOn(rocketchip, `rocket-dsp-utils`, `api-config-chipsalliance`, dsptools)
  .settings(libraryDependencies ++= rocketLibDeps.value)
  .settings(
     allDependencies := {
      // drop specific maven dependencies in subprojects in favor of Chipyard's version
      val dropDeps = Seq(("edu.berkeley.cs", "rocket-dsptools"))
      allDependencies.value.filterNot { dep =>
        dropDeps.contains((dep.organization, dep.name))
      }
    },
    commonSettings)*/

