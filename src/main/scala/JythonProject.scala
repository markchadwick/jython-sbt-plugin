package jython.sbt

import sbt._

/**
 * Trait to mix in when dealing with Jython code in an SBT project. This take
 * care of ensuring python files are put in the right directories at build time,
 * and are monitered by changes by sbt tasks which need to. Dependencies are
 * resolver in a somewhat cripped easy_install manner.
 */
trait JythonProject extends JythonPaths {

  /*
  def copyMainJythonResourcesAction =
    FileUtilities.syncPaths(mainJythonResources, mainJythonOutputPath)

  def copyTestJythonResourcesAction =
    syncPathsTask(testJythonResources, testJythonOutputPath)
  */
}
