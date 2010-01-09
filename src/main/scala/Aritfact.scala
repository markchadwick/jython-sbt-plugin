package jython.sbt

class Repository(var index: String) {
  def %(artifactName: String) = Artifact(this, artifactName)
}

object Repository {
  lazy val defaultRepository = new Repository("http://pypi.python.org/simple")
  def apply() = defaultRepository
}

case class Artifact(repository: Repository, name: String) {
  val defaultVersion = new Version(this, None)
  var version: Version = defaultVersion

  def %(name: String) = Version(this, Some(name))
}

case class Version(artifact: Artifact, name: Option[String]) {
}
