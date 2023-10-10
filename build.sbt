val scala3Version = "3.1.1"

lazy val caos = project.in(file("lib/caos"))
  .enablePlugins(ScalaJSPlugin)
  .settings(scalaVersion := scala3Version)

lazy val ccsLang = project.in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "ccs",
    version := "0.1.0",
    scalaVersion := scala3Version,
    scalaJSUseMainModuleInitializer := true,
    Compile / mainClass := Some("ccs.frontend.Main"),
    Compile / fastLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "lib" / "caos"/ "tool" / "js" / "gen",
    libraryDependencies ++= Seq(
      ("org.typelevel" %%% "cats-parse" % "0.3.4")
    )
  )
  .dependsOn(caos)