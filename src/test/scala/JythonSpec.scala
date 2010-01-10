package jython.sbt

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class JythonSpec extends FlatSpec with ShouldMatchers {
  val JythonHome = "/home/mchadwick/jython2.5.1/bin"
  val JythonExe  = "%s/jython".format(JythonHome)
  "Jython" should "have specs" is (pending)

  /*
  "Jython" should "should find the jython home" in {
    Jython.jythonHome should equal (JythonHome)
  }

  it should "find the jython executable" in {
    Jython.jython should equal (JythonExe)
  }
  */
}
