package jython.sbt

import _root_.sbt._

/**
 * JythonProject mixing to have a nose runner run Jython tests. Because this
 * mixin adds a new dependency, an <cc>update</cc> must be issued between mixing
 * in the train and running the tests (unless your codebase already depends on
 * nose). Failing to do this will probably throw an error about not being able
 * to fine <cc>nosetests</cc>
 */
trait NoseTests extends JythonProject {
  easy_install("nose")

  lazy val nosetestsExecutablePath = jythonPackagePath / "nosetests"

  protected def nosetestTask(testRoot: Path) = task {
    val args = nosetestsExecutablePath.absolutePath ::
               testJythonOutputPath.absolutePath :: 
               "-sv" :: Nil

    Jython.execute(jythonHome, args, StdoutOutput) match {
      case 0 => None
      case i => Some("nosetests Failed with error: "+ i)
    }
  }

  protected def nosetestAction = nosetestTask(testJythonPath)

  lazy val nosetests = nosetestAction
                        .dependsOn(registerJythonPathAction, testCompile,
                                   copyResources, copyTestResources)
                        .describedAs("Run nosetests")
}
