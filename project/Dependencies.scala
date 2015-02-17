import sbt._
import Keys._

object Dependencies {
  object V {
    val playVersion = "2.3.8"
    val sbtCoreNextVersion = "0.1.1"
  }

  def sbtPluginExtra(module:ModuleID):ModuleID =
    Defaults.sbtPluginExtra(module, "0.13", "2.10")

  def playForkRunPlugin = sbtPluginExtra("com.typesafe.play" % "sbt-fork-run-plugin" % V.playVersion)
  def sbtCoreNextPlugin = sbtPluginExtra("org.scala-sbt" % "sbt-core-next" % V.sbtCoreNextVersion)
}