import sbt._
import Keys._
import com.typesafe.sbt.SbtGit._

object SbtAdBuild extends Build {
  def baseVersions: Seq[Setting[_]] = versionWithGit

  lazy val sbtAd = Project(
    id = "sbt-ad",
    base = file("."),
    settings = defaultSettings ++ noPublishSettings,
    aggregate = Seq(sbtAdMain, sbtAdPlay)
  )

  lazy val sbtAdMain = Project(
    id = "sbt-ad-main",
    base = file("ad"),
    settings = defaultSettings ++ Seq(
      name := "sbt-ad-main",
      libraryDependencies ++= Seq(Dependencies.sbtCoreNextPlugin)
    )
  )

  lazy val sbtAdPlay = Project(
    id = "sbt-ad-play",
    base = file("play-ad"),
    dependencies = Seq(sbtAdMain),
    settings = defaultSettings ++ Seq(
      name := "sbt-ad-play",
      libraryDependencies ++= Seq(Dependencies.playForkRunPlugin)
    )
  )

  lazy val typesafeIvyReleases = Resolver.url("Typesafe Ivy Releases Repo", new URL("http://repo.typesafe.com/typesafe/releases/"))(Resolver.ivyStylePatterns)

  lazy val typesafeIvySnapshots = Resolver.url("Typesafe Ivy Snapshots Repo", new URL("http://private-repo.typesafe.com/typesafe/ivy-snapshots/"))(Resolver.ivyStylePatterns)

  lazy val defaultSettings: Seq[Setting[_]] = Defaults.defaultSettings ++ baseVersions ++ Seq(
    sbtPlugin := true,
    organization := "com.typesafe.sbtad",
    version <<= version in ThisBuild,
    publishMavenStyle := false,
    publishTo := Some(typesafeIvySnapshots),
    resolvers += typesafeIvyReleases,
    libraryDependencies ++= Seq(Dependencies.junit, Dependencies.junitInterface)
  )

  lazy val noPublishSettings: Seq[Setting[_]] = Seq(
    publish := {},
    publishLocal := {}
  )
}
