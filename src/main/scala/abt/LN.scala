package abt

import scala.collection.immutable.Vector
import scala.Unit
import scala.Predef.???

import scalaz._
import scalaz.syntax.equal._

/** Locally nameless terms with operators in `O`, variables `V` and sorts `S` */
sealed trait LN[S, V, O]

object LN {
  final case class FreeVar[S, V, O](v: V, s: S) extends LN[S, V, O]
  final case class BoundVar[S, V, O](c: Coord, s: S) extends LN[S, V, O]
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

      def check[M[_, _]](view: View[V, O, T], valence: Valence[S])
                        (implicit ME: MonadError[M, E],
                                  MV: MonadVar[M[E, ?], V],
                                  SE: Equal[S])
                        : M[E, T] = {

        val Valence(sorts, sigma) = valence

        view match {
          case Var(x) =>
            ME.map(expectNoBindings(valence))(_ => FreeVar(x, sigma))

          case Abs(xs, e) =>
            ME.bind(infer[M[E, ?]](e)) { case (Valence(_, tau), _) =>
              ME.map(expectSortsEq(sigma, tau))(_ => Abstraction(???, ???))
            }

          case App(theta, es) => ???
        }
      }

      def infer[M[_]](t: T)(implicit MV: MonadVar[M, V])
                     : M[(Valence[S], View[V, O, T])] = ???

      ////

      private def expectNoBindings[M[_, _]](v: Valence[S])
                                           (implicit ME: MonadError[M, E])
                                           : M[E, Unit] = {
        if (v.vars.isEmpty) ME.point(())
        else ME.raiseError(expectedNoBindings(v))
      }

      private def expectSortsEq[M[_, _]](s1: S, s2: S)
                                        (implicit ME: MonadError[M, E], S: Equal[S])
                                        : M[E, Unit] = {
        if (s1 === s2) ME.point(())
        else ME.raiseError(sortMismatch(s1, s2))
      }

      private def expectValenceEq[M[_, _]](v1: Valence[S], v2: Valence[S])
                                          (implicit ME: MonadError[M, E], S: Equal[S])
                                          : M[E, Unit] = {
        if (v1 === v2) ME.point(())
        else ME.raiseError(valenceMismatch(v1, v2))
      }
    }
}
