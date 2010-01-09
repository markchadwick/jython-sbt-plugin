package jython.sbt

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class ArtifactSpec extends FlatSpec with ShouldMatchers {

  "An Artifact" should "have be able to be constructed" in {
    val artifact = Artifact("django")
    artifact.name should equal ("django")
  }

  it should "have a default version" in {
    val artifact = Artifact("django")
    val version = artifact.version
    version should equal (Version(artifact, None))
  }

  it should "have a DSL version shortcut" in {
    val version = Artifact("django") % "1.1"

    version.artifact should equal (Artifact("django"))
    version.name should equal (Some("1.1"))
  }
}
