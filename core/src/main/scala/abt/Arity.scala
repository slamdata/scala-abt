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
