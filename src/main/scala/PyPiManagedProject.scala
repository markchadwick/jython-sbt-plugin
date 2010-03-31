package jython.sbt

import _root_.sbt._
import java.net.URL
import scala.collection.mutable.HashMap
import scala.collection.mutable.Set
import scala.collection.mutable.MultiMap

/**
 * Manage Pypi dependencies in a JythonProject. The format given is idential to
 * the one easy_install accepts. For example, <cc>"threadpool"</cc> for the most
 * recent version, <cc>"threadpool == 0.2"</cc> for an exact version, and so on
 * and so forth.
 *
 * If you'd like for a given dependency to be pulled from a location other that
 * pyi, simply use the <cc>from</cc> keyword after the <cc>easy_install</cc>
 * declaration. For example:
 * <code>
 *    val django = easy_install("django &gt;= 1.0")
 *    val internal = easy_install("internal_suite") from "http://local/repo/"
 * </code>
 */
class JythonDependency(val query: String, var url: Option[String]) {
  val pypiUrl = "http://pypi.python.org/simple"

  def this(query: String) = this(query, None)
  def from(url: String) = this.url = Some(url)

  def repoUrl = url match {
    case None => new URL(pypiUrl)
    case Some(u) => new URL(u)
  }

  override def toString = query
}

trait PyPiManagedProject extends BasicManagedProject with JythonPaths {
  private var jythonDependences: List[JythonDependency] = Nil

  def jythonHome: Path

  def easy_install(dependency: JythonDependency) = {
    jythonDependences ::= dependency
    dependency
  }

  def updateJythonDependencies(sitePackages: Path) = {
    val depsByLoc = new HashMap[URL, Set[JythonDependency]]
                        with MultiMap[URL, JythonDependency]

    jythonDependences.foreach(dep => depsByLoc add (dep.repoUrl, dep))

    depsByLoc.find(dep => {
      val (location, dependencies) = dep
      val queries = dependencies.map(_.query).toList
      log.info("Installing %s from %s".format(queries.mkString(", "), location))
      Jython.easyInstall(queries, location, sitePackages, jythonHome, log) != 0
    }) match {
      case None => 
      case Some(failedDep) =>
        log.warn("Error installing %s from %s".format(failedDep._2, failedDep._1))
    }

    None
  }

  lazy val updateJythonAction  = task {
    updateJythonDependencies(jythonPackagePath)
  }

  override def mainResources =
    thirdPartyJythonResources +++ super.mainResources

  implicit def stringToDependency(name: String) =
    new JythonDependency(name, None)

  override def updateAction =
    super.updateAction.dependsOn(updateJythonAction)  
}

