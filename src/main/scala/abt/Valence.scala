package abt

import scala.collection.immutable.List

import scalaz._
import scalaz.std.tuple._
import scalaz.std.list._
import scalaz.syntax.show._

/** Describes the sort of an argument to an operator along with the number
  * and sorts of the variables bound within it.
  */
final case class Valence[Sort](vars: List[Sort], sort: Sort)

object Valence {
  implicit def valenceOrder[S: Order]: Order[Valence[S]] =
    Order.orderBy(v => (v.sort, v.vars))

  implicit def valenceShow[S: Show]: Show[Valence[S]] =
    Show.shows(v => v.vars.shows + v.sort.shows)
}
