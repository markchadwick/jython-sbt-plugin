package jython.sbt

case class Artifact(name: String) {
  val defaultVersion = new Version(this, None)
  var version: Version = defaultVersion

  def %(name: String) = Version(this, Some(name))
}

case class Version(artifact: Artifact, name: Option[String]) {
}
