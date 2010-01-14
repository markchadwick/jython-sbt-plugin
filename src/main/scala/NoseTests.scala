package jython.sbt

import _root_.sbt._

trait NoseTests extends JythonProject {
  easy_install("nose")

  lazy val nosetestsExecutablePath = jythonPackagePath / "nosetests"

  protected def nosetestTask(testRoot: Path) = task {
    val args = nosetestsExecutablePath.absolutePath ::
               testJythonOutputPath.absolutePath :: Nil

    Jython.execute(jythonHome, args, log) match {
      case 0 => None
      case i => Some("nosetests Failed with error: "+ i)
    }
  }

  protected def nosetestAction = nosetestTask(testJythonPath)

  lazy val nosetests = nosetestAction
                        .dependsOn(testCompile, copyResources, copyTestResources)
                        .describedAs("Run nosetests")

}
