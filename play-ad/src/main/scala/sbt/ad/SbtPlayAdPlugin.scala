package sbt.ad

import sbt._
import Keys._
import play.Play
import sbt.plugins.BackgroundRunPlugin
import sbt.BackgroundJobServiceKeys
import play.sbt.forkrun.{ PlayForkRun, PlayForkOptions }
import play.forkrun.protocol.ForkConfig
import PlayForkRun.autoImport._

object SbtAdPlayPlugin extends AutoPlugin {
  import SbtAdPlugin.autoImport._

  object autoImport {
    val AppDynamicsPlay = config("appdynamicsplay").extend(AppDynamics)
  }

  import autoImport._

  override def trigger = AllRequirements

  override def requires = SbtAdPlugin && Play && PlayForkRun && BackgroundRunPlugin

  lazy val defaultAdPlaySettings: Seq[Def.Setting[_]] = {
  	Seq(
      javaOptions <++= SbtAdPlugin.javaOptions,
      PlayForkRunKeys.playForkOptions <<= adPlayForkOptionsTask,
      BackgroundJobServiceKeys.backgroundRun in ThisProject <<= PlayForkRun.backgroundForkRunTask)
  }

  def adPlayForkOptionsTask: Def.Initialize[Task[PlayForkOptions]] = Def.task {
    val in = (PlayForkRunKeys.playForkOptions in ThisProject).value
    val jo = (javaOptions in AppDynamicsPlay).value
    in.copy(jvmOptions = in.jvmOptions ++ jo)
  }

  override def projectSettings = inConfig(AppDynamicsPlay)(defaultAdPlaySettings) ++ SbtAdPlugin.projectSettings
}