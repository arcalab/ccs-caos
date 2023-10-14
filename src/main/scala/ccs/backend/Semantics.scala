package ccs.backend

import caos.sos.SOS
import ccs.backend.Semantics.St
import ccs.syntax.{Program, Show}
import ccs.syntax.Program.*
import Term.*

/** Small-step semantics for both commands and boolean+integer expressions.  */
object Semantics extends SOS[Action,St]:

  type St = System

  /** When is a state terminal: there are no states marked as terminal */
  override def accepting(s: St): Boolean = false

  /** What are the set of possible evolutions (label and new state) */
  def next[A>:Action](st: St): Set[(A, St)] = st.main match
    case End => Set()
    case Proc(p) => next(st(st.defs.getOrElse(p,End)))
    case Prefix(act,t) => Set(act -> st(t))
    case Choice(t1,t2) =>
      next(st(t1)) ++ next(st(t2))
    case Par(t1, t2) =>
      val nx1 = next(st(t1))
      val nx2 = next(st(t2))
      (for (n,s)<-nx1 yield n->st(Par(s.main,t2))) ++
      (for (n,s)<-nx2 yield n->st(Par(t1,s.main))) ++
      (for (n1,s1)<-nx1; (n2,s2)<-nx2 if dual(n1,n2)
        yield Tau -> st(Par(s1.main,s2.main)))
//    case Rename(t, f) =>
    case Restr(t,r) =>
      for (n,s) <- next(st(t)) if allowed(n,r)
      yield n -> st(Restr(s.main,r))

//      next(System(st.defs, t2))
    case _ => Set() // cannot evolve

  private def dual(a1:Action, a2:Action): Boolean = (a1,a2) match
    case (In(a),Out(b)) if a==b => true
    case (Out(a),In(b)) if a==b => true
    case _ => false

  private def allowed(a:Action, r:Set[String]): Boolean = a match
    case In(x) if r(x) => false
    case Out(x) if r(x) => false
    case _ => true

