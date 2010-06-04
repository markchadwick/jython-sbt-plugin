Jython SBT Plugin
=================
Jython SBT Plugin is, oddly enough, a Jython plugin for [Simple Build
Tool](http://code.google.com/p/simple-build-tool/) that makes integrating Python
code into your JVM project a little bit easier. It manages your paths, figures
out dependencies, and runs tests. There's a lot missing (how about a console?!),
but works well enough to use.

Using Jython SBT Plugin
-----------------------
To start using this plugin, you first have to tell SBT to use it as a plugin. If
you don't have any plugins yet, you'll need to edit
`project/plugins/Plugins.scala`. It should look something like this:

    import sbt._

    class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
      val jython = "com.hipstersinc" % "jython-sbt" % "0.1.9" from
        "http://github.com/downloads/markchadwick/jython-sbt-plugin/jython-sbt-0.1.9.jar"
    }

The next time you start sbt, it should set that up. If it explodes, try
downloading the source, and executing `sbt publish-local` to get it into your
local repository. Take note that I'm pretty lazy, and the particular version
mentioned above probably isn't recent.

Next, mixin the `JythonProject` trait into your project, and define where your
local Jython install is. You have to do this so Jython can bootstrap itself to
resolve dependencies. I'd recommend installing the most recent Jython, though
others may work.

    import sbt._
    import jython.sbt._

    class Project(info: ProjectInfo) extends DefaultProject(info)
                                     with JythonProject {

        def jythonHome = Path.fromFile("/opt/jython")
    }

Then you're ready to go. Your source files are expected to be in
`src/main/python`, and your test files `src/test/python`.  Consult `JythonPaths`
for the methods to override if you like to see these in different places. On
build, your files will synchronized to `target/python`, and the test files will
be in `target/test-python`.

The paths will be managed for you, so you should be able to import JVM classes
directly in your Jython code.

Managing Dependencies
---------------------
Dependencies are managed through `easy_install`. If you're familiar with
[setuptools](http://pypi.python.org/pypi/setuptools), you'll find nothing
shocking here. The dependencies will be managed in your project's
`lib/site-packages` directory. These dependencies are isolated on a per-project
basic, so globally installed packages, or packages installed in other projects
may not work. This is by design.

Right. So, installing dependencies. Let's say your project needed any recent
version of paycheck, httplib2 0.6.0, and a secret_sauce package stored on your
internal build server. Your project definition may look like this:

    import sbt._
    import jython.sbt._

    class Project(info: ProjectInfo) extends DefaultProject(info)
                                     with JythonProject {

        def jythonHome = Path.fromFile("/opt/jython")

        easy_install("paycheck")
        easy_install("httplib2 == 0.6.0")
        easy_install("secret_sauce") from "http://local/build/server"
    }

The next time you issue an `update` command to SBT, you'll see that you have a
shiny new `lib/site-packages` containing all those dependencies. Python-only
dependencies work fine, but Jython isn't going to perform any miracles if you
try to install something with C extensions.

Running Tests
-------------
There is an included mixin to run `nosetests` for you. If you'd like to have
nose run your test suite, simply mix in the `NoseTests` trait. You'll see that
you now have a `nosetests` SBT action.

After mixing in the trait, you will need to issue an `update`, as you now have a
dependency on `nose` which needs to be installed.

By default, this will walk through your `src/test/python` directory to find
things that look like tests, and execute them. For more information on exactly
what sorts of patterns its looking for, consult the [nose
docs](http://somethingaboutorange.com/mrl/projects/nose/0.11.1/).
