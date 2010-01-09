package jython.sbt

import java.io.File

object Jython {
  val JythonExecutable = "jython"

  lazy val jythonFile = findInPath(JythonExecutable).getOrElse(
    throw new Exception("Couldn't find executable: %s".format(JythonExecutable)))

  lazy val jython = jythonFile.getAbsolutePath
  lazy val jythonHome = jythonFile.getParent

  private def findInPath(exeName: String): Option[File] = {
    System.getenv("PATH").split(File.pathSeparator)
                         .map(new File(_).listFiles.toList)
                         .flatMap(f => f)
                         .find(_.getName == exeName)
  }
}
