// External Github dependencies
val ClientVer = "v1.0"
val FUtilsVer = "v1.2"
lazy val ClientLoc = "https://github.com/Rhodolfo/RhoClient.git#%s".format(ClientVer)
lazy val FUtilsLoc = "https://github.com/Rhodolfo/RhoFileUtils.git#%s".format(FUtilsVer)
lazy val rhoClient = RootProject(uri(ClientLoc))
lazy val rhoFUtils = ProjectRef(uri(FUtilsLoc),"filefuncs")
lazy val rhoFCheck = ProjectRef(uri(FUtilsLoc),"checkpoints")

// Common settings
lazy val commonSettings = Seq(
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  organization := "com.upax-research",
  version := "0.1.0",
  scalacOptions := Seq("-feature","-deprecation"),
  scalaVersion := "2.12.1")

// JSON parsing library
lazy val json = Seq(libraryDependencies += "net.liftweb" %% "lift-json" % "3.0.1")

// Projects
lazy val coppel = (project in file("coppel"))
  .settings(commonSettings: _*)
  .settings(name := "Coppel")
  .settings(json: _*)
  .dependsOn(rhoClient)
  .dependsOn(rhoFUtils)
  .dependsOn(rhoFCheck)
