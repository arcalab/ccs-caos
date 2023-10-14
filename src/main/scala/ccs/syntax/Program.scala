package ccs.syntax

import caos.sos.{HasTaus}

/**
 * Internal structure to represent commands in a simple while language
 *
 * @author José Proença
 */

object Program:

  /** A CCS term */
  enum Term:
    case End
    case Proc(p:String)
    case Prefix(act:Action,t:Term)
    case Choice(t1:Term, t2:Term)
    case Par(t1:Term, t2:Term)
    case Rename(t:Term, f:String=>String)
    case Restr(t:Term, acts:Set[String])

  trait Action extends HasTaus
  case class Out(a:String) extends Action:
    val isTau = false
  case class In(a:String) extends Action:
    val isTau = false
  case object Tau extends Action:
    val isTau = true

  case class System(defs: Map[String,Term], main:Term, toCompare:Option[Term]):
    def apply(newMain:Term) = System(defs,newMain,toCompare)


  //////////////////////////////
  // Examples and experiments //
  //////////////////////////////

  object Examples:
    import Program.Term._


    val p1: Term =
      Prefix(In("x"),End)

