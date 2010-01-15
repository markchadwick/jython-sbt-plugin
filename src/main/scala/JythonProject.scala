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
  Jython.registerPath(mainCompilePath)
  Jython.registerPath(testCompilePath)
  Jython.registerPath(mainDependencies.all.get.toList:_*)

  def jythonHome = Path.fromFile("/opt/jython")

  protected def copyMainJythonResourcesAction =
    syncPathsTask(mainJythonResources, mainJythonOutputPath)

  protected def copyTestJythonResourcesAction =
    syncPathsTask(testJythonResources, testJythonOutputPath)

  protected def setupJythonEnvAction = task {
    Jython.setupJythonEnv(jythonHome)
    None
  }

  protected def jythonConsoleAction = interactiveTask {
    Jython.execute(jythonHome, Nil, StdoutOutput) match {
      case 0 => None
      case i => Some("Jython Console Failed with error: "+ i)
    }
  }


  lazy val copyJythonResources     = copyMainJythonResourcesAction
  lazy val copyJythonTestResources = copyTestJythonResourcesAction
  lazy val setupJythonEnv          = setupJythonEnvAction
  lazy val jythonConsole = jythonConsoleAction
                            .dependsOn(testCompile, copyResources, copyTestResources)
                            .describedAs("Start an interactive Jython Console")

  override def copyResourcesAction =
    super.copyResourcesAction.dependsOn(copyJythonResources)

  override def copyTestResourcesAction =
    super.copyTestResourcesAction.dependsOn(copyJythonTestResources)

  override def compileAction =
    super.compileAction.dependsOn(setupJythonEnv)
}
