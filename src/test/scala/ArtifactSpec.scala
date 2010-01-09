package jython.sbt

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class ArtifactSpec extends FlatSpec
                   with ShouldMatchers
                   with JythonResolver {

  "An Artifact" should "have be able to be constructed" in {
    val artifact = Artifact(Repository(), "django")
    artifact.name should equal ("django")
  }

  it should "have a default version" in {
    val artifact = Artifact(Repository(), "django")
    val version = artifact.version
    version should equal (Version(artifact, None))
  }

  it should "have a DSL version shortcut" in {
    val version = Artifact(Repository(), "django") % "1.1"

    version.artifact should equal (Artifact(Repository(), "django"))
    version.name should equal (Some("1.1"))
  }

  it should "resolve the python mixin" in {
    python should not be (null)
  }

  it should "have a DSL for setting the repository" in {
    val artifact = python % "django"
    artifact.name should equal ("django")
    artifact.version should equal (Version(artifact, None))
  }

  it should "have a DSL for setting rep, art, and version" in {
    val version = python % "django" % "1.1"
     
    version.artifact.name should equal ("django")
    version.name should equal (Some("1.1"))
  }
}
