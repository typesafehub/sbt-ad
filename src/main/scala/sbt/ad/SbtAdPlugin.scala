package sbt.ad

import sbt._
import Keys._
import Def.Initialize
import scala.collection.mutable.ArrayBuffer
import java.io.File

object SbtAdPlugin extends AutoPlugin {

  lazy val defaultNrSettings: Seq[Def.Setting[_]] = Seq(
    inTask(run)(Seq(runner <<= adRunner)).head,

    mainClass in run <<= mainClass in run in Compile,

    unmanagedClasspath <<= unmanagedClasspath in Runtime,
    managedClasspath <<= managedClasspath in Runtime,
    internalDependencyClasspath <<= internalDependencyClasspath in Runtime,
    externalDependencyClasspath <<= Classpaths.concat(unmanagedClasspath, managedClasspath),
    dependencyClasspath <<= Classpaths.concat(internalDependencyClasspath, externalDependencyClasspath),
    exportedProducts <<= exportedProducts in Runtime,
    fullClasspath <<= Classpaths.concatDistinct(exportedProducts, dependencyClasspath),

    UIKeys.backgroundRunMain <<= SbtBackgroundRunPlugin.backgroundRunMainTask(fullClasspath, runner in run),
    UIKeys.backgroundRun <<= SbtBackgroundRunPlugin.backgroundRunTask(fullClasspath, mainClass in run, runner in run)
  )

  object autoImport {
    val AppDynamics = config("appdynamics").extend(Compile)

    val appDynamicsAgentJar = SettingKey[String]("AppDynamics agent jar file.")
    val appDynamicsAgentTierName = SettingKey[String]("AppDynamics tier name.")
    val appDynamicsAgentNodeName = SettingKey[String]("AppDynamics node name.")
    val appDynamicsAgentApplicationName = SettingKey[String]("AppDynamics application name.")
    val appDynamicsAgentRuntimeDir = SettingKey[String]("AppDynamics runtime directory.")
    val appDynamicsAgentAccountName = SettingKey[String]("AppDynamics account name.")
    val appDynamicsAgentAccountAccessKey = SettingKey[String]("AppDynamics account access key.")
    val appDynamicsControllerHostName = SettingKey[String]("AppDynamics host name.")
    val appDynamicsControllerPort = SettingKey[String]("AppDynamics port.")
    val appDynamicsControllerSslEnabled = SettingKey[String]("AppDynamics SSL enabled.")
  }

  import autoImport._

  private def exists(path: String): Boolean = (new File(path)).exists

  private[ad] def verifyFileSettings(errors: ArrayBuffer[String], settingName: String, settingPath: String): ArrayBuffer[String] = {
   if (settingPath == "") {
      errors += "Set '" + settingName + " in AppDynamics := \"<filename>\"' in your build to the location of the file."
    } else if (!exists(settingPath)) {
      errors += "The specified file '" + settingPath + 
        "' does not exist. Please make sure the location is correctly set with '" + settingName + " in AppDynamics := \"<filename>\"'"
    }

    errors
  }

  private[ad] def verifyParameterSettings(errors: ArrayBuffer[String], settingName: String, settingValue: String): ArrayBuffer[String] = {
    if (settingValue == "") {
      errors += "Missing value for '" + settingName + "'. Make sure to set '" + settingName + " in AppDynamics := \"<value>\"' in your build configuration file."
    }

    errors
  }

  private def adRunner: Initialize[Task[ScalaRun]] = Def.task {        
    def verifySettings() {
      val errors = ArrayBuffer.empty[String]
      verifyFileSettings(errors, "appDynamicsAgentJar", appDynamicsAgentJar.value)  
      verifyParameterSettings(errors, "appDynamicsAgentTierName", appDynamicsAgentTierName.value)
      verifyParameterSettings(errors, "appDynamicsAgentNodeName", appDynamicsAgentNodeName.value)
      verifyParameterSettings(errors, "appDynamicsAgentApplicationName", appDynamicsAgentApplicationName.value)
      verifyFileSettings(errors, "appDynamicsAgentRuntimeDir", appDynamicsAgentRuntimeDir.value)  
      verifyParameterSettings(errors, "appDynamicsAgentAccountName", appDynamicsAgentAccountName.value)
      verifyParameterSettings(errors, "appDynamicsAgentAccountAccessKey", appDynamicsAgentAccountAccessKey.value)  
      verifyParameterSettings(errors, "appDynamicsControllerHostName", appDynamicsControllerHostName.value)  
      verifyParameterSettings(errors, "appDynamicsControllerPort", appDynamicsControllerPort.value)  
      verifyParameterSettings(errors, "appDynamicsControllerSslEnabled", appDynamicsControllerSslEnabled.value)  

      if (errors.size > 0) {
        throw new RuntimeException(errors.mkString("\n"))
      } 
    }

    verifySettings()

    val adJavaOptions: Seq[String] = Seq(
      s"-javaagent:${appDynamicsAgentJar.value}",
      s"-Dappdynamics.agent.tierName=${appDynamicsAgentTierName.value}",
      s"-Dappdynamics.agent.nodeName=${appDynamicsAgentNodeName.value}",
      s"-Dappdynamics.agent.applicationName=${appDynamicsAgentApplicationName.value}",
      s"-Dappdynamics.agent.runtime.dir=${appDynamicsAgentRuntimeDir.value}",
      s"-Dappdynamics.agent.accountName=${appDynamicsAgentAccountName.value}",
      s"-Dappdynamics.agent.accountAccessKey=${appDynamicsAgentAccountAccessKey.value}",
      s"-Dappdynamics.controller.hostName=${appDynamicsControllerHostName.value}",
      s"-Dappdynamics.controller.port=${appDynamicsControllerPort.value}",
      s"-Dappdynamics.controller.ssl.enabled=${appDynamicsControllerSslEnabled.value}")

    val forkConfig = ForkOptions(javaHome.value, outputStrategy.value, Seq.empty, Some(baseDirectory.value), adJavaOptions, connectInput.value)
        
    if (fork.value) new ForkRun(forkConfig) else throw new RuntimeException("This plugin can only be run in forked mode")
  }

  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  override val projectSettings = 
    Seq(
      appDynamicsAgentJar := "",
      appDynamicsAgentTierName := "",
      appDynamicsAgentNodeName := "",
      appDynamicsAgentApplicationName := "",
      appDynamicsAgentRuntimeDir := "",
      appDynamicsAgentAccountName := "",
      appDynamicsAgentAccountAccessKey := "",
      appDynamicsControllerHostName := "",
      appDynamicsControllerPort := "",
      appDynamicsControllerSslEnabled := "") ++
    inConfig(AppDynamics)(defaultNrSettings)
}
