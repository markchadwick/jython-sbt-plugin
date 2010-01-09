import sbt._
import jython.sbt.JythonProject

class PassingProject(info: ProjectInfo) extends DefaultProject(info)
                                        with JythonProject {
  override def jythonHome = Path.fromFile("/opt/jython")
}
