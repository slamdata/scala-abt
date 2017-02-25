package abt

import scala.Int

import scalaz._
import scalaz.std.tuple._
import scalaz.std.anyVal._
import scalaz.syntax.contravariant._

final class Coord private (val i: Int, val j: Int) {
  def shiftRight: Coord = new Coord(i, j + 1)
  def shiftDown: Coord = new Coord(i + 1, j)
}

object Coord {
  val origin = new Coord(0, 0)

  implicit val coordEqual: Equal[Coord] =
    Equal.equalBy(c => (c.i, c.j))

  implicit val coordShow: Show[Coord] =
    Show[(Int, Int)].contramap(c => (c.i, c.j))
}

