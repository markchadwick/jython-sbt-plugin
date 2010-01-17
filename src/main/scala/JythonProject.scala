package jython.sbt

import _root_.sbt._

/**
 * Trait to mix in when dealing with Jython code in an SBT project. This take
 * care of ensuring python files are put in the right directories at build time.
 * Follow the maven convention, python source files live in
 * <cc>src/main/python</cc>, and test files live in <cc>src/test/python</cc>.
 * Their respective "build" files are in python and test-python.
 *
 * This implementation, at the moment, is perhaps a little generous about what
 * it will include in the classpath at compile and test time. Given that this is
 * all quickly hacked out, I expect this to change as problems are discovered.
 *
 * In order to have a project mix in <cc>JythonProject</cc>, it must define the
 * <cc>jythonHome</cc> method. An example implementation may be something like:
 * <code>
 *    def jythonHome = Path.fromFile("/opt/jython")
 * </code>
 */
trait JythonProject extends BasicScalaProject
                    with JythonPaths
                    with PyPiManagedProject {

  def jythonHome: Path

  /**
   * A task to register a magic set of paths which were discovered by trial and
   * error. Each path registered will be in the (class|python)path when forking
   * a Jython project. This may be a bit excessive.
   */
  def registerJythonPathAction = task {
    Jython.registerPath(jythonPackagePath)
    Jython.registerPath(mainJythonOutputPath)
    Jython.registerPath(mainCompilePath)
    Jython.registerPath(testCompilePath)
    testClasspath.get.foreach(dep => Jython.registerPath(dep))
    mainDependencies.scalaJars.get.foreach(dep => Jython.registerPath(dep))

    None
  }

  protected def copyMainJythonResourcesAction =
    syncPathsTask(mainJythonResources, mainJythonOutputPath)

  protected def copyTestJythonResourcesAction =
    syncPathsTask(testJythonResources, testJythonOutputPath)

  protected def setupJythonEnvAction = task {
    Jython.setupJythonEnv(jythonHome)
    None
  }

  /**
   * Start a Jython console. At the moment, this is broken. Fortunatly, both the
   * Scala and Jython REPLs use JLine, so integrating the Jython console may not
   * be an impossibe task. At a code level, they are quite similar.
   */
  protected def jythonConsoleAction = interactiveTask {
    Jython.execute(jythonHome, Nil, StdoutOutput) match {
      case 0 => None
      case i => Some("Jython Console Failed with error: "+ i)
    }
  }

  lazy val jythonConsole = jythonConsoleAction
                            .dependsOn(testCompile, copyResources, copyTestResources)
                            .describedAs("Interactive Jython Console (Broken)")

  lazy val copyJythonResources     = copyMainJythonResourcesAction
  lazy val copyJythonTestResources = copyTestJythonResourcesAction
  lazy val setupJythonEnv          = setupJythonEnvAction dependsOn(registerJythonPathAction)

  override def copyResourcesAction =
    super.copyResourcesAction.dependsOn(copyJythonResources)

  override def copyTestResourcesAction =
    super.copyTestResourcesAction.dependsOn(copyJythonTestResources)

  override def compileAction =
    super.compileAction.dependsOn(setupJythonEnv)
}
