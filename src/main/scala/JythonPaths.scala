package jython.sbt

import _root_.sbt._

/**
 * Defines the expected paths for a Jython project. This largely follows the
 * Maven convention, and puts source files in <cc>src/main/python</cc> and test
 * files in <cc>src/test/python</cc>. Their party dependencies will be put in
 * <cc>lib/site-packages</cc>.
 *
 * Follow the SBT convention, any of these paths can be overriden by overriding
 * the method which strings them together. Look a <cc>mainJythonPath</cc> for
 * example.
 */
trait JythonPaths extends MavenStyleScalaPaths {
  def jythonHome: Path

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

  def sitePackagesPath = DefaultJythonContainerPath / DefaultJythonPackagesName
  def jythonEggs = descendents(sitePackagesPath ##, "*.egg")
  def jythonLibraryPath = jythonHome / "Lib"

  def mainJythonResources = descendents(mainJythonPath ##, "*")
  def testJythonResources = descendents(testJythonPath ##, "*")
  def jythonLibraryResources = descendents(jythonLibraryPath ##, "*")
  def jythonEggResources = 
    jythonEggs.get.foldLeft(Path.emptyPathFinder)((eggs, egg) => {
      /*
      eggs +++ ((descendents(egg ##, "*") --- descendents(egg ##, "*.class")) ---
                 descendents(egg / "EGG-INFO", "*"))
      */
      eggs +++ descendents(egg ##, "*")
    })
}
