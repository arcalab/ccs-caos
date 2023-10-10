package ccs.frontend

import caos.frontend.Site.initSite
import ccs.syntax.{Parser, Program}
import ccs.frontend.CaosConfig
import ccs.syntax.Program.System

/** Main function called by ScalaJS' compiled javascript when loading. */
object Main {
  def main(args: Array[String]):Unit =
    initSite[System](CaosConfig)
}