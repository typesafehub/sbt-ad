package sbt.ad

import sbt._
import Keys._
import play.{Play, PlayInternalKeys}

object SbtAdPlayPlugin extends AutoPlugin with PlayInternalKeys {
  import SbtAdPlugin.autoImport._

  object autoImport {
    val AppDynamicsPlay = config("appdynamicsplay").extend(AppDynamics)
    val adPlayRunner = taskKey[BackgroundJobHandle]("Run play dev-mode runner with AppDynamics agent")
  }

  import autoImport._

  override def trigger = AllRequirements

  override def requires = SbtAdPlugin && Play

  lazy val defaultAdPlaySettings: Seq[Def.Setting[_]] = {
    Seq(adPlayRunner <<= adPlayRunnerTask) ++
  	Seq(
      UIKeys.backgroundRunMain in ThisProject := adPlayRunner.value,
      UIKeys.backgroundRun in ThisProject := adPlayRunner.value)
  }

  def adPlayRunnerTask: Def.Initialize[Task[BackgroundJobHandle]] = Def.task {
    playBackgroundRunTaskBuilder.value(SbtAdPlugin.javaOptions.value)
  } 

  override def projectSettings = inConfig(AppDynamicsPlay)(defaultAdPlaySettings) ++ SbtAdPlugin.projectSettings
}