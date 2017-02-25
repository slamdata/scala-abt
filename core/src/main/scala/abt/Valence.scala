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

import slamdata.Predef.Vector

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

