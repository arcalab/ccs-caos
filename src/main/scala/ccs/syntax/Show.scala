package ccs.syntax

import ccs.syntax.Program
import Program.{Term,Action,System}
import Term.*



/**
 * List of functions to produce textual representations of commands
 */
object Show:

  def apply(s: System): String =
    apply(s.defs)+
    apply(s.main)
  def apply(defs: Map[String,Term]): String =
    if defs.isEmpty then ""
    else "let\n"+
      (for (p,t)<-defs yield s"  $p = ${apply(t)}").mkString("\n") +
      "\nin\n  "

  /** Pretty expression */
  def apply(e: Term): String = e match
    case Term.End => "0"
    case Term.Proc(p) => p
    case Term.Prefix(act, Term.End) => apply(act)
    case Term.Prefix(act, t) => s"${apply(act)}.${applyP(t)}"
    case Term.Choice(t1, t2) => s"${applyP(t1)}+${applyP(t2)}"
    case Term.Par(t1, t2) => s"${applyP(t1)} | ${applyP(t2)}"
    case Term.Rename(t, f) => applyP(t)+"[...]"
    case Term.Restr(t, acts) => applyP(t)+"\\"+acts.mkString("{",",","}")

  def applyP(e:Term): String = e match
    case _:(End.type|Proc|Prefix) => apply(e)
    case _ => s"(${apply(e)})"

  def apply(a:Action): String = a match
    case Action.Out(a) => a+"'"
    case Action.In(a) => a
    case Action.Tau => "tau"

      // private def applyP(e: Term): String = e match
      //   case _:(Var|Val) => apply(e)
      //   case _ => s"(${apply(e)})"

      /** Converts a lambda term into a mermaid diagram reflecting its structure. */
  def mermaid(e: Term): String = "graph TD\n" + term2merm(e).mkString("\n")

      /** Builds nodes and arcs, using a set structure to avoid repetition. */
  private def term2merm(e: Term): Set[String] = e match
    case Term.End => Set("0")
    case Term.Proc(p) => Set(p)
    case Term.Prefix(act, t) => ???
    case Term.Choice(t1, t2) => ???
    case Term.Par(t1, t2) => ???
    case Term.Rename(t, f) => ???
    case Term.Restr(t, acts) => ???


    //    case Var(x) => Set(s"  ${e.hashCode()}([\"$x\"])")
//    case _ => Set()
// case App(e1, e2) =>
    //   Set(s"  ${e.hashCode()}([\"${Show(e)}\"])") ++
    //   term2merm(e1) ++
    //   term2merm(e2) ++
    //   Set(s"  ${e.hashCode()} -->|app-left| ${e1.hashCode()}",
    //       s"  ${e.hashCode()} -->|app-right| ${e2.hashCode()}")

/*
graph TD
    A([Christmas]) -->|Get money| B(Go shopping)
    B --> C{Let me think}
    C -->|One| D[Laptop]
    C -->|Two| E[iPhone]
    C -->|Three| F[fa:fa-car Car]
*/