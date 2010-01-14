import sbt._
import jython.sbt._

class PassingProject(info: ProjectInfo) extends DefaultProject(info)
                                        with JythonProject with NoseTests {
  override def jythonHome = Path.fromFile("/opt/jython")
}
