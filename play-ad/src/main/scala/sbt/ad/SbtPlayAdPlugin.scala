package sbt.ad

import sbt._
import Keys._
import play.PlayInternalKeys

object SbtAdPlayPlugin extends AutoPlugin with PlayInternalKeys {
  import SbtAdPlugin._

  override def trigger = AllRequirements

  override def requires = SbtAdPlugin

  lazy val defaultNrSettings: Seq[Def.Setting[_]] = Seq(
    inTask(run)(Seq(runner <<= SbtAdPlugin.adRunner)).head,

    UIKeys.backgroundRunMain in ThisProject := playBackgroundRunTaskBuilder.value((Keys.javaOptions in Runtime).value),

    UIKeys.backgroundRun in ThisProject := playBackgroundRunTaskBuilder.value((Keys.javaOptions in Runtime).value)
  )
}