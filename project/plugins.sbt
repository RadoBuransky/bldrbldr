logLevel := Level.Warn

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  Classpaths.sbtPluginReleases)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")

addSbtPlugin("com.sksamuel.scoverage" %% "sbt-scoverage" % "0.95.3")

addSbtPlugin("com.sksamuel.scoverage" %% "sbt-coveralls" % "0.0.5")