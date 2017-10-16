import ReleaseTransformations._

organization := "com.github.Uchibori3"

name := "mfcloud-invoice"

scalaVersion := "2.12.3"

// dependencies version
lazy val akkaVersion     = "2.4.19"
lazy val akkaHttpVersion = "10.0.10"
lazy val circeVersion    = "0.9.0-M1"

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-http"                   % akkaHttpVersion,
  "com.typesafe.akka"          %% "akka-stream"                 % akkaVersion,
  "io.circe"                   %% "circe-generic"               % circeVersion,
  "io.circe"                   %% "circe-parser"                % circeVersion,
  "de.heikoseeberger"          %% "akka-http-circe"             % "1.19.0-M2",
  "org.scala-lang.modules"     %% "scala-java8-compat"          % "0.8.0",
  "com.typesafe.scala-logging" %% "scala-logging"               % "3.7.2",
  "com.typesafe.akka"          %% "akka-http-testkit"           % akkaHttpVersion % "test",
  "com.typesafe.akka"          %% "akka-stream-testkit"         % akkaVersion % "test",
  "org.scalatest"              %% "scalatest"                   % "3.0.4" % "test",
  "org.scalamock"              %% "scalamock-scalatest-support" % "3.6.0" % "test"
)

parallelExecution in Test := false

fork in Test := true

enablePlugins(ScalafmtPlugin)

scalafmtOnCompile := true

scalafmtTestOnCompile := true

scalacOptions ++= Seq(
  // https://tpolecat.github.io/2017/04/25/scalac-flags.html
  "-deprecation",
  "-encoding",
  "utf-8",
  "-explaintypes",
  "-feature",
  "-language:existentials",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xcheckinit",
  "-Xfatal-warnings",
  "-Xfuture",
  "-Xlint:adapted-args",
  "-Xlint:by-name-right-associative",
  "-Xlint:constant",
  "-Xlint:delayedinit-select",
  "-Xlint:doc-detached",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:missing-interpolator",
  "-Xlint:nullary-override",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit",
  "-Xlint:package-object-classes",
  "-Xlint:poly-implicit-overload",
  "-Xlint:private-shadow",
  "-Xlint:stars-align",
  "-Xlint:type-parameter-shadow",
  "-Xlint:unsound-match",
  "-Yno-adapted-args",
  "-Ypartial-unification",
  "-Ywarn-dead-code",
  "-Ywarn-extra-implicit",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused:implicits",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:params",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates",
  "-target:jvm-1.8"
)

scalacOptions in (Compile, console) ~= (_.filterNot(
  Set(
    "-Ywarn-unused:imports",
    "-Xfatal-warnings"
  )
))

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8", "-Xlint")

licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/Uchibori3/mfcloud-invoice-scala"))

publishMavenStyle := true

publishArtifact in Test := false

publishTo := Some(
  if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
  else Opts.resolver.sonatypeStaging
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/Uchibori3/mfcloud-invoice-scala"),
    "scm:git@github.com:Uchibori3/mfcloud-invoice-scala.git"
  )
)

developers := List(
  Developer(
    id = "uchibori3",
    name = "Takuya Uchibori",
    email = "uch180r13@gmail.com",
    url = url("https://github.com/Uchibori3")
  )
)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("+publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("+sonatypeRelease", _)),
  pushChanges
)
