import sbt._

class JythonPluginProject(info: ProjectInfo) extends PluginProject(info) 
                                             with test.ScalaScripted {
  lazy val ivy = "org.apache.ivy" % "ivy" % "2.0.0"
  lazy val scalatest = "org.scalatest" % "scalatest" % "1.0" % "test"
}
