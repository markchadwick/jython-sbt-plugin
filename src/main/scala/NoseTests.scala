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
  easy_install("nose==0.11.3")

  lazy val nosetestsExecutablePath = jythonPackagePath / "nosetests"
  lazy val nosetestsTestPath = testResourcesOutputPath // testJythonOutputPath

  protected def runNose(args: Seq[String]): Option[String] = {
    val noseArgs = nosetestsExecutablePath :: args.toList

    Jython.execute(noseArgs.map(_.toString), jythonHome, StdoutOutput) match {
      case 0 => None
      case i => Some("nosetests Failed with error: "+ i)
    }
  }

  protected lazy val testDeps = testCompile ::
                                copyResources ::
                                copyTestResources ::
                                Nil

  private lazy val nosetestAction = task({ args =>
    val testDirectory = nosetestsTestPath.absolutePath
    val (localFiles, flags) = args.partition(_.startsWith("./"))

    val files = if(localFiles.isEmpty) testDirectory :: Nil
                else localFiles.map(testDirectory + "/" + _)

    task {
      runNose(flags ++ files)
    }.dependsOn(testDeps:_*)
  }).completeWith(testJythonResources.get
                    .filter(! _.isDirectory)
                    .map(_.toString).toList)

  lazy val nosetests = nosetestAction.describedAs("Run nosetests")
}
