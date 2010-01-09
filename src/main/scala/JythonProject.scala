package jython.sbt

import _root_.sbt._

/**
 * Trait to mix in when dealing with Jython code in an SBT project. This take
 * care of ensuring python files are put in the right directories at build time,
 * and are monitered by changes by sbt tasks which need to. Dependencies are
 * resolver in a somewhat cripped easy_install manner.
 */
trait JythonProject extends ScalaProject
                    with JythonPaths {

  def jythonHome = Path.fromFile("/tmp/jython")

  lazy val jython = "org.python" % "jython" % Jython.jar

  def copyMainJythonResourcesAction =
    syncPathsTask(mainJythonResources, mainJythonOutputPath)

  def copyTestJythonResourcesAction =
    syncPathsTask(testJythonResources, testJythonOutputPath)

  lazy val copyJythonResources = copyMainJythonResourcesAction

}
