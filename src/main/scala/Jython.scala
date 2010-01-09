package jython.sbt

import java.io.File
import java.io.FileOutputStream
import java.net.URL
import _root_.sbt.FileUtilities
import _root_.sbt.Fork
import _root_.sbt.Logger
import _root_.sbt.Path
import _root_.sbt.LoggedOutput

object Jython {
  val easySetupUrl = new URL("http://peak.telecommunity.com/dist/ez_setup.py")

  def jythonMain = "org.python.util.jython"

  def jythonJar(jythonHome: Path) = jythonHome / "jython.jar"
  def jythonExe(jythonHome: Path) = jythonHome / "jython"
  def jythonLib(jythonHome: Path) = jythonHome / "Lib"

  private def mkPath(paths: Seq[String]): String =
    paths.reduceLeft(_ + File.pathSeparator + _)

  /*
  val JythonExecutable = "jython"

  lazy val jythonFile = findInPath(JythonExecutable).getOrElse(
    throw new Exception("Couldn't find executable: %s".format(JythonExecutable)))

  lazy val jython = jythonFile.getAbsolutePath
  lazy val jythonHome = jythonFile.getParent
  lazy val version = "2.5.1"
  lazy val jar = "file://%s/jython.jar".format(jythonHome)

  private def findInPath(exeName: String): Option[File] = {
    System.getenv("PATH").split(File.pathSeparator)
                         .map(new File(_).listFiles.toList)
                         .flatMap(f => f)
                         .find(_.getName == exeName)
  }
  */

  def execute(jythonHome: Path, args: List[String], sitePackages: Path, log: Logger): Int = {
    val classpath = mkPath(jythonJar(jythonHome).absolutePath ::
                           jythonLib(jythonHome).absolutePath ::
                           Nil)

    val javaArgs = "-Xmx512m" :: "-Xss1024k" ::
                   "-classpath" :: classpath ::
                   "-Dpython.home=%s".format(jythonHome.absolutePath) ::
                   "-Dpython.executable=%s".format(jythonExe(jythonHome).absolutePath) ::
                   jythonMain ::
                   args

    println("* FORKING JAVA WITH:"+ javaArgs)
    val ja = javaArgs.reduceLeft(_ +" "+ _)
    println("* Args: "+ ja)

    Fork.java(None, javaArgs, None, jythonEnv(jythonHome, sitePackages), LoggedOutput(log))
  }

  def jythonEnv(jythonHome: Path, sitePackages: Path) =
    Map("JYTHON_HOME" -> jythonHome.absolutePath,
        "PYTHONPATH"  -> sitePackages.absolutePath)

  def easyInstall(query: String, repo: URL, sitePackages: Path, jythonHome: Path, log: Logger) = {
    val easySetupPath = sitePackages / "ez_setup.py"
    ensureSetupTools(sitePackages, easySetupPath, log)
    val args = easySetupPath.absolutePath ::
               "--find-links"  :: repo.toString ::
               "--always-copy" ::
               "--install-dir" :: sitePackages.absolutePath ::
               query :: Nil

    execute(jythonHome, args, sitePackages, log)
  }

  private def ensureSetupTools(sitePackages: Path, ezSetup: Path, log: Logger) =
    ezSetup.exists match {
      case true => log.info("ez_setup exists. We're cool")
      case false => bootstrapSetupTools(sitePackages, ezSetup, log)
    }

  private def bootstrapSetupTools(sitePackages: Path, ezSetup: Path, log: Logger) {
    log.warn("* ez_setup missing from %s. downloading".format(ezSetup))
    FileUtilities.createDirectory(sitePackages, log)
    val file = ezSetup.asFile
    file.createNewFile
    log.info("Downloading from %s to %s".format(easySetupUrl, file))
    FileUtilities.download(easySetupUrl, file, log)
  }

}
