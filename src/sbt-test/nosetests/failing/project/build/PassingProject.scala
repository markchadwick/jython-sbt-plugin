import sbt._
import jython.sbt.{JythonProject, NoseTests}

class PassingProject(info: ProjectInfo) extends DefaultProject(info)
                                        with JythonProject with NoseTests {
  override def jythonHome = Path.fromFile("/opt/jython")

  easy_install("paycheck == 0.4.2")
}
