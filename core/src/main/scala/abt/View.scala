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

/**
 *
 * @tparam V Type of Variables in the ADT (kind of like identifier)
 * @tparam O The underlying AST
 */
sealed trait View[V, O, A]

object View {
  /** A node that’s a variable reference */
  final case class Var[V, O, A](v: V)                 extends View[V, O, A]
  /** Abstraction is a node that binds variables */
  final case class Abs[V, O, A](vs: Vector[V], a: A)  extends View[V, O, A]
  /** A standard term in the underlying AST */
  final case class App[V, O, A](op: O, as: Vector[A]) extends View[V, O, A]

  implicit def viewFunctor[V, O]: Functor[View[V, O, ?]] =
    new Functor[View[V, O, ?]] {
      def map[A, B](va: View[V, O, A])(f: A => B): View[V, O, B] = va match {
        case Var(v)      => Var(v)
        case Abs(vs, a)  => Abs(vs, f(a))
        case App(op, as) => App(op, as map f)
      }
    }
}
