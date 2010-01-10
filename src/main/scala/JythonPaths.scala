package jython.sbt

import _root_.sbt._

trait JythonPaths extends MavenStyleScalaPaths 
                  with BasicDependencyPaths {

  val DefaultJythonSourceName   = "python"
  val DefaultJythonTestName     = "python"
  // val DefaultJythonContainerPath = managedDirectoryName
  val DefaultJythonContainerPath = "lib"
  val DefaultJythonPackagesName = "site-packages"

  def jythonSourceDirectoryName = DefaultJythonSourceName
  def jythonTestDirectoryName   = DefaultJythonTestName

  def mainJythonPath = mainSourcePath / jythonSourceDirectoryName
  def testJythonPath = testSourcePath / jythonTestDirectoryName

  def mainJythonOutputPath = outputPath / DefaultJythonSourceName
  def testJythonOutputPath = outputPath / DefaultJythonTestName

  def jythonPackagePath = DefaultJythonContainerPath / DefaultJythonPackagesName

  def mainJythonResources = descendents(mainJythonPath ##, "*")
  def testJythonResources = descendents(testJythonPath ##, "*")
}
