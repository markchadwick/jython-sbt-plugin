package jython.sbt

import java.io.File
import java.net.URL
import _root_.sbt.FileUtilities
import _root_.sbt.Fork
import _root_.sbt.LoggedOutput
import _root_.sbt.Logger
import _root_.sbt.OutputStrategy
import _root_.sbt.Path
import _root_.sbt.PathFinder

/**
 * Utility for forking Jython processes configured for this project. These
 * methods need a full Jython install to run property (even if the project has a
 * dependecy on Jython).
 */
object Jython {
  private var pythonPaths = Set.empty[Path]

  val easySetupUrl = new URL("http://peak.telecommunity.com/dist/ez_setup.py")

  def jythonMain = "org.python.util.jython"

  def jythonJar(jythonHome: Path) = jythonHome / "jython.jar"
  def jythonExe(jythonHome: Path) = jythonHome / "jython"
  def jythonLib(jythonHome: Path) = jythonHome / "Lib"

  def registerPath(pythonPath: Path) = 
    this.pythonPaths += pythonPath

  def registerJars(jars: Iterable[File]) =
    jars.foreach(jar => registerPath(Path.fromFile(jar)))

  /**
   * Given a set of params, fork a Jython process. This will use a full install
   * of Jython to run the process, which may be confusing if you have one
   * version of Jython installed, an another defined in your project (This will
   * always used the installed version).
   */
  def execute(args: List[String], classpath: PathFinder, log: Logger): Int = {
    val classpathStr = Path.makeString(classpath.get)

    val javaArgs = "-classpath" :: classpathStr ::
                   "-Dpython.path=%s".format(classpathStr) ::
                   "-Djython.path=%s".format(classpathStr) ::
                   "-Dpython.executable=/opt/jython/jython" ::
                   jythonMain :: args

    log.debug("[Jython] java %s".format(javaArgs.mkString(" ")))
    Fork.java(None, javaArgs, None, log)
  }

  def jythonEnv(jythonHome: Path) =
    Map("JYTHON_HOME"-> jythonHome.absolutePath,
        "PYTHONPATH" -> Path.makeString(pythonPaths + jythonLib(jythonHome)))

  def setupJythonEnv(jythonHome: Path) =
    Map("jython.home" -> jythonHome.absolutePath,
        "python.path" -> Path.makeString(pythonPaths + jythonLib(jythonHome)))
       .foreach(param => System.setProperty(param._1, param._2)) 

  /**
   * easy_install a dependency into the given site-packages directory. This will
   * ensure the directory is bootstrapped, and managed further dependencies of
   * the given dependency.
   *
   * @param query easy_install style query for a dependency. For example,
   *        <cc>django</cc> or <cc>myTools == 1.2</cc>.
   * @param rep pypi-like repository to fetch dependencies from. Setuptools may
   *        fall back to the main pypi repo if the given repo doesn't contain
   *        the given dependency.
   * @param sitePackages Local site-packages path to install the dependency to
   * @param jythonHome Jython install location
   * @param log Logger to dump info and errors to
   * @returns 0 if successful, otherwise 1
   */
  def easyInstall(queries: List[String], repo: URL, setuptoolsVersion: String,
                  sitePackages: Path, jythonHome: Path, log: Logger) = {

    val easySetupPath = sitePackages / "ez_setup.py"
    val easyInstallPath = sitePackages / "easy_install"
    ensureSetupTools(jythonHome, setuptoolsVersion, sitePackages, easySetupPath, log)
    val args = easyInstallPath.absolutePath ::
               "--find-links" :: repo.toString ::
               "--always-copy" ::
               "--always-unzip" ::
               "--install-dir" :: sitePackages.absolutePath ::
               "-S" :: sitePackages.absolutePath ::
               queries

    val setuptoolsPath = sitePackages /
                         "setuptools-%s-py2.5.egg".format(setuptoolsVersion)

    val classpath = (sitePackages ##) +++
                    (setuptoolsPath ##) +++ 
                    jythonJar(jythonHome) +++
                    jythonLib(jythonHome)

    execute(args, classpath, log)
  }

  /**
   * Ensures that the given <cc>sitePackages</cc> path has been bootstrapped
   * with an ezSetup file. If ezSetup file exists, this will quickly exit,
   * otherwise, it will download it from the web, and save it to the expected
   * location.
   *
   * @param sitePackages Path of the local site-packages install
   * @param ezSetup Path of the expected ez_setup boostrapping script
   * @param log Logger to dump info and errors to
   */
  private def ensureSetupTools(jythonHome: Path, setuptoolsVersion: String,
                               sitePackages: Path, ezSetup: Path, log: Logger) =
    ezSetup.exists match {
      case true => log.info("ez_setup exists")
      case false => bootstrapSetupTools(jythonHome, setuptoolsVersion,
                                        sitePackages, ezSetup, log)
    }

  /**
   * Downloads a setuptools bootstrapping script from the web. This should only
   * happen once in a project. The downloaded setup script will live in the
   * local site-packages, so cleaning dependencies may nuke this script. Once
   * this script is downloaded, it'll do a local install of setuptools to speed
   * things up in the future.
   *
   * @param sitePackages Path to a site-packages diretory, which expects to have
   *        an easy_install bootstrapping script.
   * @param ezSetup Path to download the bootstrapping scrip to
   * @param log Logger to dump output to on info and errors
   */
  private def bootstrapSetupTools(jythonHome: Path, setuptoolsVersion: String,
                                  sitePackages: Path, ezSetup: Path, log: Logger) {

    log.warn("ez_setup missing from %s. downloading".format(ezSetup))
    sitePackages.asFile.mkdirs()
    val file = ezSetup.asFile
    file.createNewFile
    log.info("Downloading from %s to %s".format(easySetupUrl, file))
    FileUtilities.download(easySetupUrl, file, log)
    log.info("Installing setuptools")

    val query = "setuptools==%s".format(setuptoolsVersion)
    val args = ezSetup.absolutePath ::
               "--find-links" :: "http://pypi.python.org/simple" ::
               "--always-copy" ::
               "--always-unzip" ::
               "--install-dir" :: sitePackages.absolutePath ::
               "-S" :: sitePackages.absolutePath ::
               query :: Nil
    val classpath = (sitePackages ##) +++
                    jythonJar(jythonHome) +++
                    jythonLib(jythonHome)
    execute(args, classpath, log)
  }
}
