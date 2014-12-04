import sbt._
import Keys._

object Dependencies {
  def playPlugin = Defaults.sbtPluginExtra("com.typesafe.play" % "sbt-plugin" % "2.3.8-6bf5e68cd80eb54e6fdfc6544748b89e3e2c096c", "0.13", "2.10")
  def uiPlugin = Defaults.sbtPluginExtra("com.typesafe.sbtrc" % "ui-interface-0-13" % "1.0-43891de56b625f1c0e810348360fee05a22445bf", "0.13", "2.10")
}