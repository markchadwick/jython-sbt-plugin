package jython.sbt

import java.io.{FileOutputStream, PrintWriter}
import _root_.sbt._

trait JythonProject extends BasicScalaProject
                    with JythonPaths
                    with PyPiManagedProject {

  override def testResources =
    testJythonResources +++ super.testResources

  override def mainResources = 
    mainJythonResources +++
    jythonEggResources +++
    jythonLibraryResrouces +++
    super.mainResources

  /*
  override def copyResourcesAction = task {
    writeProjectPathFile()
    None
  } dependsOn(super.copyResourcesAction)
  */

  /*
  private val pthFile = {
    val projectName = info.projectPath.asFile.getName
    jythonPackageInstallPath / "%s.pth".format(projectName)
  }

  protected def writeProjectPathFile() {
    if(mainResourcesOutputPath.exists) {
      val outFile = pthFile.asFile
      outFile.createNewFile
      val out = new PrintWriter(new FileOutputStream(outFile))

      out.println("import sys; sys.__plen = len(sys.path)")
      thirdPartyJythonResources.get.foreach(res => out.println(res.toString))
      out.println("import sys; new=sys.path[sys.__plen:]; del sys.path[sys.__plen:];p=getattr(sys,'__egginsert',0); sys.path[p:p]=new; sys.__egginsert = p+len(new)")
      out.close()
    }
  }
  */
}
