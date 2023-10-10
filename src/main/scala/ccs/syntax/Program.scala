package ccs.syntax

/**
 * Internal structure to represent commands in a simple while language
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

  enum Action:
    case Out(a:String)
    case In(a:String)
    case Tau

  case class System(defs: Map[String,Term], main:Term):
    def apply(newMain:Term) = System(defs,newMain)


  //////////////////////////////
  // Examples and experiments //
  //////////////////////////////

  object Examples:
    import Program.Term._
    import Program.Action._

    val p1: Term =
      Prefix(In("x"),End)

