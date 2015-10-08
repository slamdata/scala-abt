package abt

import scala.collection.immutable.Vector

import scalaz._
import scalaz.std.tuple._
import scalaz.std.vector._
import scalaz.syntax.show._

/** Describes the sort of an argument to an operator along with the number
  * and sorts of the variables bound within it.
  */
final case class Valence[Sort](vars: Vector[Sort], sort: Sort)

object Valence extends ValenceInstances {
  def noVars[S](sort: S): Valence[S] =
    Valence(Vector.empty[S], sort)

  implicit def valenceOrder[S: Order]: Order[Valence[S]] =
    Order.orderBy(v => (v.sort, v.vars))

  implicit def valenceShow[S: Show]: Show[Valence[S]] =
    Show.shows(v => v.vars.shows + v.sort.shows)
}

sealed abstract class ValenceInstances {
  implicit def valenceEqual[S: Equal]: Equal[Valence[S]] =
    Equal.equalBy(v => (v.sort, v.vars))
}

