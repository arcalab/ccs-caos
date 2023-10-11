package ccs.frontend

import caos.frontend.Configurator
import Configurator.*
import caos.view.{Code, Mermaid, Text}
import ccs.backend.*
import ccs.syntax.{Program, Show}
import Program.System

/** Object used to configure which analysis appear in the browser */
object CaosConfig extends Configurator[System]:
  val name = "Animator of a simple CCS language"
  override val languageName: String = "CCS term"

  val parser =
    ccs.syntax.Parser.parseProgram

  val examples = List(
    "coffee" -> "let\n CM = coin.coffee'.CM;\n CS = pub.coin'.coffee.CS;\nin\n (CM|CS)\\{coffee,coin}",
    "coffee-var" -> "let\n CM = coin.coffee.CM;\n CS = pub.coin.coffee.CS;\nin\n CM|CS",
    "a.0" -> "a.0",
    "a.b" -> "a.b",
    "+" -> "a.b + c.d",
    "|" -> "a.b | a'",
    "loop" -> "let P = a.b.P;\nin P",
  )

  /** Description of the widgets that appear in the dashboard. */
  val widgets = List(
//    "View parsed data" -> view(_.toString, Text), //.moveTo(1),
    "View pretty data" -> view[System](Show(_), Code("haskell")), //.moveTo(1),
    // "Diagram of the structure" -> view(Show.mermaid, Mermaid).moveTo(1),
     "Run semantics" -> steps(e=>e, Semantics, x=>Show(x.main), Show(_), Text),
    // "Run semantics (with diagrams)" -> steps(e=>e, Semantics, Show.mermaid, Mermaid),
     "Build LTS" -> lts((e:System)=>e, Semantics, x=>Show(x.main), Show(_)).expand,
     "Build LTS (explore)" -> ltsExplore(e=>e, Semantics, x=>Show(x.main), Show(_)),
    // "Build LTS - Lazy Evaluation" -> lts(e=>e, LazySemantics, Show(_)),
    // "Build LTS - Strict Evaluation" -> lts(e=>e, StrictSemantics, Show(_)),
    // "Find bisimulation: given 'A B', check if 'A ~ B'" ->
    //   compareBranchBisim(Semantics,Semantics,getApp(_).e1,getApp(_).e2,Show(_),Show(_)),
  )
