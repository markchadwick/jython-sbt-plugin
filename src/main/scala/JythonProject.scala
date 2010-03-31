package jython.sbt

import _root_.sbt._

trait JythonProject extends BasicScalaProject
                    with JythonPaths
                    with PyPiManagedProject {

  override def testResources =
    testJythonResources +++ super.testResources

  override def mainResources =
    mainJythonResources +++ thirdPartyJythonResources +++ super.mainResources
}
