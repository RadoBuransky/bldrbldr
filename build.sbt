organization := "radoburansky"

name := "jugjane"

version := "1.1-SNAPSHOT"

scalacOptions ++= Seq("-feature")

resolvers ++= Seq(
  "The Buzz Media Maven Repository" at "http://maven.thebuzzmedia.com",
  "Rhinofly Internal Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local",
  "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/")

libraryDependencies ++= Seq(
  jdbc,
  "org.imgscalr" % "imgscalr-lib" % "4.2",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.0",
  "com.typesafe" %% "play-plugins-mailer" % "2.1-RC2",
  "nl.rhinofly" %% "play-s3" % "3.3.3")

play.Project.playScalaSettings