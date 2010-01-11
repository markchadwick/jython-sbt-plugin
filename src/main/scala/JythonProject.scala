package jython.sbt

import _root_.sbt._

/**
 * Trait to mix in when dealing with Jython code in an SBT project. This take
 * care of ensuring python files are put in the right directories at build time,
 * and are monitered by changes by sbt tasks which need to. Dependencies are
 * resolver in a somewhat cripped easy_install manner.
 */
trait JythonProject extends BasicScalaProject
                    with JythonPaths
                    with PyPiManagedProject {

  Jython.registerPath(jythonPackagePath)
  Jython.registerPath(mainJythonOutputPath)

  def jythonHome = Path.fromFile("/opt/jython")

  def copyMainJythonResourcesAction =
    syncPathsTask(mainJythonResources, mainJythonOutputPath)

  def copyTestJythonResourcesAction =
    syncPathsTask(testJythonResources, testJythonOutputPath)

  def setupJythonEnvAction = task {
    Jython.setupJythonEnv(jythonHome)
    None
  }

  lazy val copyJythonResources     = copyMainJythonResourcesAction
  lazy val copyJythonTestResources = copyTestJythonResourcesAction
  lazy val setupJythonEnv          = setupJythonEnvAction

  override def copyResourcesAction =
    super.copyResourcesAction.dependsOn(copyJythonResources)

  override def copyTestResourcesAction =
    super.copyTestResourcesAction.dependsOn(copyJythonTestResources)

  override def compileAction =
    super.compileAction.dependsOn(setupJythonEnv)
}
