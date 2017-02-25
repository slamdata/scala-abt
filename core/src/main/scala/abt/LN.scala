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
import scalaz.std.vector._
import scalaz.std.anyVal._
import scalaz.syntax.equal._
import scalaz.syntax.apply._
import scalaz.syntax.foldable._

/** Locally nameless terms with operators in `O`, variables `V` and sorts `S` */
sealed trait LN[S, V, O]

object LN {
  final case class FreeVar[S, V, O](v: V, s: S)
    extends LN[S, V, O]
  final case class BoundVar[S, V, O](c: Coord, s: S)
    extends LN[S, V, O]
  final case class Abstraction[S, V, O](vs: Vector[(V, S)], t: LN[S, V, O])
    extends LN[S, V, O]
  final case class Application[S, V, O](o: O, args: Vector[LN[S, V, O]])
    extends LN[S, V, O]

  /** NB: Not inherently stack safe, depends on the underlying Monad to
    * provide stack safety.
    */
  implicit def lnAbt[S, V, O]: Abt[S, V, O, LN[S, V, O]] =
    new Abt[S, V, O, LN[S, V, O]] {
      import AbtError._
      import View._

      type T = LN[S, V, O]
      type E = AbtError[S, V]

      def check[F[_]](
        view: View[V, O, T],
        valence: Valence[S]
      )(implicit
        ME: MonadError[F, E],
        MV: MonadVar[F, V],
        O:  Operator[S, O],
        SE: Equal[S],
        SV: Equal[V]
      ): F[T] = {
        def chkInf(e: T, vlnc: Valence[S]): F[T] =
          ME.bind(infer[F](e)) { case (vlnc1, _) =>
            ME.map(expectValenceEq(vlnc, vlnc1))(_ => e)
          }

        val Valence(sorts, sigma) = valence

        view match {
          case Var(x) =>
            ME.map(expectNoBindings(valence))(_ => FreeVar(x, sigma))

          case Abs(xs, e) =>
            ME.bind(infer[F](e)) { case (Valence(_, tau), _) =>
              ME.bind(expectSortsEq(sigma, tau)) { _ =>
                if (xs.length === sorts.length)
                  ME.point(Abstraction(xs zip sorts, imprisonVariables(xs, e)))
                else
                  ME.raiseError(unequalLengths("variables", "sorts"))
              }
            }

          case App(theta, es) =>
            ME.bind(expectNoBindings(valence)) { _ =>
              val Arity(valences, tau) = O.arity(theta)
              ME.bind(expectSortsEq(sigma, tau)) { _ =>
                if (es.length === valences.length)
                  ME.map(ME.sequence((es |@| valences)(chkInf)))(Application(theta, _))
                else
                  ME.raiseError(unequalLengths("terms", "valences"))
              }
            }
        }
      }

      def infer[F[_]](
        t: T
      )(implicit
        ME: MonadError[F, E],
        MV: MonadVar[F, V],
        O:  Operator[S, O]
      ): F[(Valence[S], View[V, O, T])] = {
        def inferValence: T => Valence[S] = {
          case FreeVar(_, sigma) =>
            Valence.noVars(sigma)

          case BoundVar(_, sigma) =>
            Valence.noVars(sigma)

          case Abstraction(bindings, e) =>
            Valence(bindings map (_._2), inferValence(e).sort)

          case Application(theta, _) =>
            Valence.noVars(O.arity(theta).sort)
        }

        t match {
          case FreeVar(v, sigma) =>
            ME.point((Valence.noVars(sigma), Var(v)))

          case BoundVar(_, s) =>
            ME.raiseError(UnexpectedBoundVariable(s))

          case Abstraction(bindings, e) =>
            val vlnc1 = inferValence(e)
            def xs = MV.traverse(bindings)(b => MV.clone(b._1))
            ME.apply2(expectNoBindings(vlnc1), xs)((_, ys) =>
              (Valence(bindings map (_._2), vlnc1.sort),
               Abs(ys, liberateVariables(ys, e))))

          case Application(theta, es) =>
            ME.point((Valence.noVars(O.arity(theta).sort), App(theta, es)))
        }
      }

      ////

      private def expectNoBindings[F[_]](
        v: Valence[S]
      )(implicit
        ME: MonadError[F, E]
      ): F[Unit] = {
        if (v.vars.isEmpty) ME.point(())
        else ME.raiseError(expectedNoBindings(v))
      }

      private def expectSortsEq[F[_]](
        s1: S,
        s2: S
      )(implicit
        ME: MonadError[F, E],
        S: Equal[S]
      ): F[Unit] = {
        if (s1 === s2) ME.point(())
        else ME.raiseError(sortMismatch(s1, s2))
      }

      private def expectValenceEq[F[_]](
        v1: Valence[S],
        v2: Valence[S]
      )(implicit
        ME: MonadError[F, E],
        S: Equal[S]
      ): F[Unit] = {
        if (v1 === v2) ME.point(())
        else ME.raiseError(valenceMismatch(v1, v2))
      }

      /** Bind the variables in the given term.
        *
        * TODO: Stack safety
        */
      private def imprisonVariables(vs: Vector[V], t: T)(implicit V: Equal[V]): T = {
        def imprisonVariable(v: V, c: Coord, e: T): T =
          e match {
            case FreeVar(v1, sigma) =>
              if (v === v1) BoundVar(c, sigma) else e

            case BoundVar(_, _) => e

            case Abstraction(xs, e1) =>
              Abstraction(xs, imprisonVariable(v, c.shiftRight, e1))

            case Application(theta, es) =>
              Application(theta, es map (imprisonVariable(v, c, _)))
          }

        vs.foldMap(v => ShiftFn[T, T](imprisonVariable(v, _, _)))(Category[ShiftFn].monoid)
          .f(Coord.origin, t)
      }

      /** Replace bound variables in the given term with free variables.
        *
        * TODO: Stack safety
        */
      private def liberateVariables(vs: Vector[V], t: T): T = {
        def liberateVariable(v: V, c: Coord, e: T): T =
          e match {
            case FreeVar(_, _) => e

            case BoundVar(c1, sigma) =>
              if (c === c1) FreeVar(v, sigma) else e

            case Abstraction(xs, e1) =>
              Abstraction(xs, liberateVariable(v, c.shiftRight, e1))

            case Application(theta, es) =>
              Application(theta, es map (liberateVariable(v, c, _)))
          }

        vs.foldMap(v => ShiftFn[T, T](liberateVariable(v, _, _)))(Category[ShiftFn].monoid)
          .f(Coord.origin, t)
      }
    }
}
