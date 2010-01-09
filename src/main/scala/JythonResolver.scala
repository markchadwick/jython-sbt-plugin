package jython.sbt

import _root_.sbt.ManagedProject

trait JythonResolver extends ManagedProject {
  def python = Repository()
}

