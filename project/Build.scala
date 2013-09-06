import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "boulder-builder"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
      jdbc,
      anorm,
      "org.imgscalr" % "imgscalr-lib" % "4.2",
      "org.reactivemongo" %% "play2-reactivemongo" % "0.9",
      "com.typesafe" %% "play-plugins-mailer" % "2.1-RC2",
      "nl.rhinofly" %% "api-s3" % "3.1.0"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
      resolvers += "The Buzz Media Maven Repository" at "http://maven.thebuzzmedia.com",
      resolvers += "Rhinofly Internal Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local"
  
  )
  

}
