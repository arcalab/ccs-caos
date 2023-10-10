package ccs.syntax

import cats.parse.{LocationMap, Parser as P, Parser0 as P0}
import cats.parse.Numbers.*
import cats.syntax.all.*
import P.*
import cats.data.NonEmptyList
import cats.parse.Rfc5234.sp
import ccs.syntax.Program.*
import ccs.syntax.Program.Term.*

import scala.sys.error

object Parser :

  /** Parse a command  */
  def parseProgram(str:String):System =
    pp(program,str) match {
      case Left(e) => error(e)
      case Right(c) => c
    }

  /** Applies a parser to a string, and prettifies the error message */
  def pp[A](parser:P[A],str:String): Either[String,A] =
    parser.parseAll(str) match
      case Left(e) => Left(prettyError(str,e))
      case Right(x) => Right(x)

  /** Prettifies an error message */
  def prettyError(str:String,err:Error): String =
    val loc = LocationMap(str)
    val pos = loc.toLineCol(err.failedAtOffset) match
      case Some((x,y)) =>
        s"""at ($x,$y):
           |"${loc.getLine(x).getOrElse("-")}"
           |${("-" * (y+1))+"^\n"}""".stripMargin
      case _ => ""
    s"${pos}expected: ${err.expected.toList.mkString(", ")}\noffsets: ${
      err.failedAtOffset};${err.offsets.toList.mkString(",")}"

  // Simple parsers for spaces and comments
  /** Parser for a sequence of spaces or comments */
  val whitespace: P[Unit] = P.charIn(" \t\r\n").void
  val comment: P[Unit] = string("//") *> P.charWhere(_!='\n').rep0.void
  val sps: P0[Unit] = (whitespace | comment).rep0.void

  // Parsing smaller tokens
  def alphaDigit: P[Char] =
    P.charIn('A' to 'Z') | P.charIn('a' to 'z') | P.charIn('0' to '9') | P.charIn('_')
  def varName: P[String] =
    (charIn('a' to 'z') ~ alphaDigit.rep0).string
  def procName: P[String] =
    (charIn('A' to 'Z') ~ alphaDigit.rep0).string
  def symbols: P[String] = // symbols starting with "--" are meant for syntactic sugar of arrows, and ignored as sybmols of terms
    P.not(string("--")).with1 *>
    oneOf("+-><!%/*=|&".toList.map(char)).rep.string
  import scala.language.postfixOps


  /** A program is a command with possible spaces or comments around. */
  def program: P[System] =
    (system|term.map(x=>System(Map(),x))).surroundedBy(sps)

  def system: P[System] =
    string("let") *> sps *>
    ((defn.repSep0(sps)<*sps<*string("in")<*sps).with1 ~ term)
      .map((x,y)=>System(x.toMap,y))

  def defn:P[(String,Term)] =
    (procName <* char('=').surroundedBy(sps)) ~
      (term <* sps <* char(';'))

  def term: P[Term] = P.recursive(more =>
    termSum(more).repSep(sps *> char('|') <* sps)
      .map(l => l.toList.tail.foldLeft(l.head)((t1, t2) => Par(t1, t2)))
  )

  def termSum(more:P[Term]): P[Term] =
    termRestr(more).repSep(sps *> char('+') <* sps)
      .map(l=>l.toList.tail.foldLeft(l.head)((t1,t2)=>Choice(t1,t2)))

  def termRestr(more:P[Term]): P[Term] =
    (termSeq(more) ~ ((sps*>char('\\')*>sps*>setLbl)?))
      .map((t,r) => if r.isDefined then Restr(t,r.get) else t)

  def termSeq(more:P[Term]): P[Term] = P.recursive(t2 =>
    end | proc | pref(t2)<*sps | char('(')*>more.surroundedBy(sps)<*char(')')
  )

  def setLbl:P[Set[String]] =
    (char('{') *> sps *>
      varName.repSep(sps~char(',')~sps).map(_.toList.toSet) <*
      sps <* char('}'))

  def end: P[Term] =
    char('0').as(End)
  def proc: P[Term] =
    procName.map(Proc.apply)

  def pref(t2:P[Term]): P[Term] =
    (varName ~ (((char('\'')?)<*sps)~ ((sps *> char('.') *> t2)?)))
      .map(x =>
       if x._2._1.isDefined
       then Prefix(Action.Out(x._1),x._2._2.getOrElse(End))
       else Prefix(Action.In(x._1),x._2._2.getOrElse(End))
      )

  //  def par: P[Term] =


  // /** (Recursive) Parser for a command in the while language */
  // def expression: P[Term] = P.recursive(exprRec =>
  //   def lit:P[Term] = P.recursive(litRec =>// lambda, int, or var
  //     ((char('\\')~sps)*>varName~(string("->").surroundedBy(sps)*>exprRec))
  //       .map(x => Lam(x._1,x._2)) |
  //     (char('-')*>digits).map(x=>Val(0-x.toInt)) |
  //     digits.map(x=>Val(x.toInt)) |
  //     (char('(')*>exprRec.surroundedBy(sps)<*char(')')) |
  //     ((string("if0")~sps)*>(litRec<*sps)~(litRec<*sps)~litRec)
  //       .map(x => If0(x._1._1,x._1._2,x._2)) |
  //     varName.map(Var.apply)
  //   )
  //   def rest:P[Term => Term] =
  //     ((char('+')~sps)*>exprRec)
  //       .map(e2 => (e1 => Add(e1,e2))) |
  //     exprRec
  //       .map(e2 => (e1 => App(e1,e2)))

  //   (lit ~ (sps*>rest.?))
  //       .map(x => x._2.getOrElse(y=>y)(x._1))

  // )


  // /// Auxiliary parser combinators

  // /** Non-empty list of elements with a binary operator */
  // def listSep[A](elem:P[A],op:P[(A,A)=>A]): P[A] =
  //   (elem ~ (op.surroundedBy(sps).backtrack~elem).rep0)
  //     .map(x=>
  //       val pairlist = x._2
  //       val first = x._1;
  //       pairlist.foldLeft(first)((rest,pair) => pair._1(rest,pair._2))
  //     )

  // /** Pair of elements with a separator */
  // def binary[A,B](p1:P[A],op:String,p2:P[B]): P[(A,B)] =
  //   (p1 ~ string(op).surroundedBy(sps) ~ p2).map(x=>(x._1._1,x._2))


  //////////////////////////////
  // Examples and experiments //
  //////////////////////////////
  object Examples:
    val ex1 =
      """x:=28; while(x>1) do x:=x-1"""
