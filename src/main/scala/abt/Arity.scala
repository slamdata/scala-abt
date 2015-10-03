package abt

import scala.collection.immutable.List

import scalaz._
import scalaz.std.tuple._
import scalaz.std.list._
import scalaz.syntax.show._

/** Specifies an operator with sort `sort` accepting `args` arguments with
  * cooresponding valences.
  */
final case class Arity[Sort](args: List[Valence[Sort]], sort: Sort)

// TODO: Lenses
object Arity {
  implicit def arityOrder[S: Order]: Order[Arity[S]] =
    Order.orderBy(a => (a.sort, a.args))

  implicit def arityShow[S: Show]: Show[Arity[S]] =
    Show.shows(a => a.args.map(_.shows).mkString("(",",",")") + a.sort.shows)
}
