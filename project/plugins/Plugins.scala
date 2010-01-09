import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val sbtTest = "org.scala-tools.sbt" % "test" % "0.5.6"
}
