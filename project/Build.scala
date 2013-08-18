import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "boulder-builder"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
      jdbc,
      anorm,
      "postgresql" % "postgresql" % "9.1-901.jdbc4",
      "org.imgscalr" % "imgscalr-lib" % "4.2",
      "org.reactivemongo" %% "play2-reactivemongo" % "0.9"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
      resolvers ++= Seq(
          "The Buzz Media Maven Repository" at "http://maven.thebuzzmedia.com")
  )

}
