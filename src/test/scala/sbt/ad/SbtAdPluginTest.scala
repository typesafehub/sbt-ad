package sbt.ad

import org.junit.{ Test, Rule }
import org.junit.Assert._
import scala.collection.mutable.ArrayBuffer

class SbtAdPluginTest {

  @Test
  def verifyMissingFileValue() {
    val errorBuffer = SbtAdPlugin.verifyFileSettings(ArrayBuffer.empty[String], "MyName", "")
    assertTrue("has one error message", errorBuffer.size == 1)
    assertTrue("has correct error message", errorBuffer.head.contains("Set 'MyName in AppDynamics"))
  }

  @Test
  def verifyMissingFile() {
    val errorBuffer = SbtAdPlugin.verifyFileSettings(ArrayBuffer.empty[String], "MyName", "MyFile")
    assertTrue("has one error message", errorBuffer.size == 1)
    assertTrue("has correct error message ", errorBuffer.head.contains("The specified file 'MyFile' does not exist."))
  }

  @Test
  def verifyCorrectFileValue() {
    val errorBuffer = SbtAdPlugin.verifyFileSettings(ArrayBuffer.empty[String], "MyName", classOf[SbtAdPluginTest].getProtectionDomain.getCodeSource.getLocation.getPath)
    assertTrue("has no error message", errorBuffer.size == 0)
  }

  @Test
  def verifyMissingParameterValue() {
    val errorBuffer = SbtAdPlugin.verifyParameterSettings(ArrayBuffer.empty[String], "MyName", "")
    assertTrue("has one error message", errorBuffer.size == 1)
    assertTrue("has correct error message ", errorBuffer.head.contains("Missing value for 'MyName'."))
  }

  @Test
  def verifyCorrectParameterValue() {
    val errorBuffer = SbtAdPlugin.verifyParameterSettings(ArrayBuffer.empty[String], "MyName", "MyValue")
    assertTrue("has no error message", errorBuffer.size == 0)
  }
}