package jython.sbt

import _root_.sbt._

trait PyPiManagedProject extends BasicManagedProject {
  case class JythonDependency(query: String)
  private var jythonDependences: List[JythonDependency] = Nil

  def jythonPackagePath: Path

  def easy_install(packageName: String) = 
    jythonDependences ::= JythonDependency(packageName)

  def updateJythonDependencies(sitePackages: Path) = {
    println("* Updating Jython *")
    jythonDependences.foreach(println)
    println("* Done")
    None
  }

  lazy val updateJythonAction  = task {
    updateJythonDependencies(jythonPackagePath)
  }

  override def updateAction =
    super.updateAction.dependsOn(updateJythonAction)  
}

