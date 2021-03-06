import sbt._
import jython.sbt.JythonProject

class PassingProject(info: ProjectInfo) extends DefaultProject(info)
                                        with JythonProject {
  override def jythonHome = Path.fromFile("/opt/jython")

  easy_install("paycheck == 0.4.2")
  // easy_install("messaging") from "https://dev1.invitemedia.com/dist"
}
