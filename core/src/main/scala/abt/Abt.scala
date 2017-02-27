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

import scalaz._

/** @tparam S Sort (syntactic category)
  * @tparam V Type of variables
  * @tparam O underlying AST
  * @tparam T Abt concrete instance
  */
trait Abt[S, V, O, T] {
  def check[F[_]](
    view: View[V, O, T],
    valence: Valence[S]
  )(implicit
    ME: MonadError[F, AbtError[S, V]],
    MV: MonadVar[F, V],
    O:  Operator[S, O],
    SE: Equal[S],
    SV: Equal[V]
  ): F[T]

  def infer[F[_]](
    t: T
  )(implicit
    ME: MonadError[F, AbtError[S, V]],
    MV: MonadVar[F, V],
    O:  Operator[S, O]
  ): F[(Valence[S], View[V, O, T])]
}

object Abt {
  def apply[S, V, O, T](implicit A: Abt[S, V, O, T]): Abt[S, V, O, T] = A
}
