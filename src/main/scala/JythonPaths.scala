package jython.sbt

import _root_.sbt._

trait JythonPaths extends MavenStyleScalaPaths 
                  with BasicDependencyPaths {

  val DefaultJythonSourceName    = "python"
  val DefaultJythonTestName      = "python"
  val DefaultJythonContainerPath = "lib"
  val DefaultJythonPackagesName  = "site-packages"

  val DefaultJythonOutputSourceName = "python"
  val DefaultJythonOutputTestName   = "test-python"

  def jythonSourceDirectoryName = DefaultJythonSourceName
  def jythonTestDirectoryName   = DefaultJythonTestName

  def mainJythonPath = mainSourcePath / jythonSourceDirectoryName
  def testJythonPath = testSourcePath / jythonTestDirectoryName

  def mainJythonOutputPath = outputPath / DefaultJythonOutputSourceName
  def testJythonOutputPath = outputPath / DefaultJythonOutputTestName

  def jythonPackagePath = DefaultJythonContainerPath / DefaultJythonPackagesName

  def mainJythonResources = descendents(mainJythonPath ##, "*")
  def testJythonResources = descendents(testJythonPath ##, "*")
}
