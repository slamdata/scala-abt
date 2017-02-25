package abt

import scala.collection.immutable.Vector

import scalaz._
import scalaz.std.tuple._
import scalaz.std.vector._
import scalaz.syntax.show._

/** Specifies an operator with sort `sort` accepting `args` arguments with
  * cooresponding valences.
  */
final case class Arity[Sort](args: Vector[Valence[Sort]], sort: Sort)

// TODO: Lenses
object Arity extends ArityInstances {
  implicit def arityOrder[S: Order]: Order[Arity[S]] =
    Order.orderBy(a => (a.sort, a.args))

  implicit def arityShow[S: Show]: Show[Arity[S]] =
    Show.shows(a => a.args.map(_.shows).mkString("(",",",")") + a.sort.shows)
}

sealed abstract class ArityInstances {
  implicit def arityEqual[S: Equal]: Equal[Arity[S]] =
    Equal.equalBy(a => (a.sort, a.args))
}
