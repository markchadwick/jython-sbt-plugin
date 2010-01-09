import sbt._

class JythonPluginProject(info: ProjectInfo) extends PluginProject(info) {
  lazy val scalatest = "org.scalatest" % "scalatest" % "1.0" % "test"
}
