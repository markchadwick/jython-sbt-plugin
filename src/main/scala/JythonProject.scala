package jython.sbt

import java.io.{FileOutputStream, PrintWriter}
import _root_.sbt._

trait JythonProject extends BasicScalaProject
                    with JythonPaths
                    with PyPiManagedProject {

  override def testResources =
    testJythonResources +++ super.testResources

  override def mainResources = 
    super.mainResources +++
    eggResources +++
    jythonLibraryResrouces +++
    mainJythonResources

  private def eggResources = 
    jythonEggResources ---
      descendents(jythonEggResources ##, "site.py*")
}
