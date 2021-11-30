ThisBuild / scalaVersion := "3.0.2"
Global / semanticdbEnabled := true
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val webpage = project
  .in(file("webpage"))
  .enablePlugins(
    ScalaJSPlugin,
    ScalaJSBundlerPlugin,
    ScalablyTypedConverterPlugin
  )
  .settings(
    scalaJSUseMainModuleInitializer := true,
    stIgnore += "vega-view",
    requireJsDomEnv := true,
    webpackEmitSourceMaps := false, // to keep compile / reload cycle fast
    webpackDevServerPort := 3000,
    webpack / version := "4.46.0",
    startWebpackDevServer / version := "3.11.2",
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    webpackDevServerExtraArgs := Seq("--inline"),
    webpackEmitSourceMaps := false, // to keep compile / reload cycle fast
    webpackDevServerPort := 3000,
    webpackConfigFile := Some(baseDirectory.value / "webpack.config.js"),
    useYarn := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.2.0" cross CrossVersion.for3Use2_13,

    ),
    /*   Compile / npmDependencies += "vega-embed" -> "6.18.2",
    Compile / npmDependencies += "vega" -> "5.19.1",
    Compile / npmDependencies += "vega-lite" -> "4.17.0",
    Compile / npmDependencies += "vega-view" -> "5.10.1",
     */ Compile / npmDevDependencies += "html-webpack-plugin" -> "4.0.0",
    Compile / npmDevDependencies += "style-loader" -> "2.0.0",
    Compile / npmDevDependencies += "css-loader" -> "5.0.1",
    Compile / npmDevDependencies += "mini-css-extract-plugin" -> "1.3.4",
    Compile / npmDevDependencies += "webpack-merge" -> "4.1.0"
  )
  .dependsOn(core.js)

lazy val webserver = project
  .in(file("webserver"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.2.6" cross CrossVersion.for3Use2_13,
      "com.typesafe.akka" %% "akka-stream" % "2.6.16" cross CrossVersion.for3Use2_13,
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.postgresql" % "postgresql" % "42.2.24",
      "io.getquill" %% "quill-jdbc" % "3.10.0.Beta1.6",      
    ),
/*     Compile / resourceGenerators += Def.task {
      val source = (webpage / Compile / scalaJSLinkedFile).value.data
      val dest = (Compile / resourceManaged).value / "assets" / "main.js"
      IO.copy(Seq(source -> dest))
      Seq(dest)
    }, */
    run / fork := true
  )
  .dependsOn(core.jvm)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-generic" % "0.14.1",
      "io.circe" %%% "circe-parser" % "0.14.1",
      "org.scalameta" %%% "munit" % "0.7.29" % Test,
    )
  )

val scalafixRules = Seq(
  "OrganizeImports",
  "DisableSyntax",
  "LeakingImplicitClassVal",
  "ProcedureSyntax",
  "NoValInForComprehension"
).mkString(" ")
