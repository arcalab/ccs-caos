package ccs.frontend

import caos.frontend.{Configurator, Documentation}
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
    "bisim-1" -> "a.b+b.a ~ a|b" -> "Simple example to check that 2 processes are bisimilar",
    "bisim-2" -> "a.(b.c+b.d) ~ a.b.(c+d)" -> "Simple example of 2 non-bisimilar processes",
    "bisim-coffee" -> "let\n CM = coin.coffee'.CM;\n CS = pub.coin'.coffee.CS;\n P = pub.P;\nin\n (CM|CS)\\{coffee,coin} ~ P",
  )

  /** Description of the widgets that appear in the dashboard. */
  val widgets = List(
//    "View parsed data" -> view(_.toString, Text), //.moveTo(1),
    "View pretty data" -> view[System](Show(_), Code("haskell")), //.moveTo(1),
    // "Diagram of the structure" -> view(Show.mermaid, Mermaid).moveTo(1),
     "Run semantics" -> steps(e=>e, Semantics, Show.justTerm, Show(_), Text),
    // "Run semantics (with diagrams)" -> steps(e=>e, Semantics, Show.mermaid, Mermaid),
     "Build LTS" -> lts((e:System)=>e, Semantics, Show.justTerm, Show(_)).expand,
     "Build LTS (explore)" -> ltsExplore(e=>e, Semantics, x=>Show(x.main), Show(_)),
    // "Build LTS - Lazy Evaluation" -> lts(e=>e, LazySemantics, Show(_)),
    // "Build LTS - Strict Evaluation" -> lts(e=>e, StrictSemantics, Show(_)),
    "Find branching bisimulation (given a program \"A ~ B\")" ->
      compareBranchBisim(Semantics,Semantics,
       (e:System)=>System(e.defs,e.main,None),
       (e:System)=>System(e.defs,e.toCompare.getOrElse(Program.Term.End),None),
        Show.justTerm, Show.justTerm, Show(_)),
  )

  override val footer: String =
    """Simple animator of Milner's CCS calculus for concurrent systems, meant both for teaching CCS and to exemplify the
      | CAOS libraries, used to generate this website:
      | <a target="_blank" href="https://github.com/arcalab/CAOS">
      | https://github.com/arcalab/CAOS</a>.""".stripMargin

  private val ccsRules: String =
    """The operational rules that we use to reduce a CCS term are provided below.
      | They can also be found, for example, in the slides available at
      | <a target="_blank" href="https://lmf.di.uminho.pt/CyPhyComp2223/slides/2-behaviour.pdf#page=27">
      | https://lmf.di.uminho.pt/CyPhyComp2223/slides/2-behaviour.pdf#page=27</a>.
      |
      |<pre>
      |  --------------------(act)
      |  label.P --label--> P
      |
      |    P1 --label--> P'
      |  --------------------(sum-1)
      |  P1 + P2 --label-> P'
      |
      |    P2 --label--> P'
      |  --------------------(sum-2)
      |  P1 + P2 --label-> P'
      |
      |      P1 --label--> P'
      |  -------------------------(com-1)
      |  P1 | P2 --label-> P' | P2
      |
      |      P2 --label--> P'
      |  -------------------------(com-2)
      |  P1 | P2 --label-> P1 | P'
      |
      |  P1 --a--> P1'  P2 --a'--> P2'
      |  -----------------------------(com-3)
      |    P1 | P2 --tau-> P1' | P2'
      |
      |  P --a--> P'  a,a' not in L
      |  --------------------------(rest)
      |       P\L --label-> P'\L
      |</pre>""".stripMargin

  override val documentation: Documentation = List(
    languageName -> "More information on the syntax of CCS" ->
      """A program <code>prog</code> in CCS is given by the following grammar:
        |<pre>
        |  prog ::= term
        |         | "let" (ProcName "=" term ";")* "in" term
        |  term ::= 0
        |         | ProcName
        |         | LabelName
        |         | LabelName "." term
        |         | term "+" term
        |         | term "|" term
        |         | term "\" "{" ActionName ("," ActionName)* "}"
        |</pre>
        |
        |The pseudo-terminal <code>ProcName</code> is a string that starts with an upper-case letter,
        |<code>ActionName</code> is a string that starts with a lower-case letter, and
        |<code>LabelName</code> is an <code>ActionName</code>, possibly followed by a prime (<code>'</code>),
        |or the keyword <code>"tau"</code>.
        |""".stripMargin,
    "Build LTS" -> "More information on the operational rules used here" -> ccsRules,
    "Build LTS (explore)" -> "More information on the operational rules used here" -> ccsRules,
    "Run semantics" -> "More information on the operational rules used here" -> ccsRules,
    "Find branching bisimulation (given a program \"A ~ B\")" -> "More information on this widget" ->
      ("<p>When the main program consists of 2 processes separated by <code>~</code>, this widget " +
      "will search for a (branching) bisimulation between these 2 processes, providing either a " +
      "concrete bisimulation or an explanation of where it failed.</p>"+
      "<p>When only a process is mentioned in the program, it checks if it is bisimilar to the empty process <code>0</code>.</p>"),
  )

