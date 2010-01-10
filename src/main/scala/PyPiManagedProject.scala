package jython.sbt

import _root_.sbt._
import java.net.URL

class JythonDependency(val query: String, var url: Option[String]) {
  val pypiUrl = "http://pypi.python.org/simple"

  def this(query: String) = this(query, None)
  def from(url: String) = this.url = Some(url)

  def repoUrl = url match {
    case None => new URL(pypiUrl)
    case Some(u) => new URL(u)
  }
}

trait PyPiManagedProject extends BasicManagedProject {
  private var jythonDependences: List[JythonDependency] = Nil

  def jythonPackagePath: Path
  def jythonHome: Path

  def easy_install(dependency: JythonDependency) = {
    jythonDependences ::= dependency
    dependency
  }

  def updateJythonDependencies(sitePackages: Path) = {
    jythonDependences.find(dep => {
      log.info("Installing: "+ dep)
      Jython.easyInstall(dep.query, dep.repoUrl, sitePackages, jythonHome, log) != 0
    }) match {
      case None => 
      case Some(failedDep) =>
        log.warn("Error installing "+ failedDep.query)
    }

    None
  }

  lazy val updateJythonAction  = task {
    updateJythonDependencies(jythonPackagePath)
  }

  implicit def stringToDependency(name: String) =
    new JythonDependency(name, None)

  override def updateAction =
    super.updateAction.dependsOn(updateJythonAction)  
}

