/*
 * Copyright 2014–2017 SlamData Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package abt

import slamdata.Predef._

import scalaz._
import scalaz.std.tuple._
import scalaz.std.anyVal._
import scalaz.syntax.contravariant._

final class Coord private (val i: Int, val j: Int) {
  def shiftRight: Coord = new Coord(i, j + 1)
  def shiftDown: Coord = new Coord(i + 1, j)
}

object Coord {
  val origin: Coord = new Coord(0, 0)

  implicit val coordEqual: Equal[Coord] =
    Equal.equalBy(c => (c.i, c.j))

  implicit val coordShow: Show[Coord] =
    Show[(Int, Int)].contramap(c => (c.i, c.j))
}

