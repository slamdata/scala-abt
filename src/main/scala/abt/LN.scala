package abt

import scala.collection.immutable.List

import scalaz._, Scalaz._

/** Locally nameless terms with operators in `O`, variables `V` and sorts `S` */
sealed trait LN[S, V, O]

object LN {
  final case class FreeVar[S, V, O](v: V, s: S) extends LN[S, V, O]
  final case class BoundVar[S, V, O](c: Coord, s: S) extends LN[S, V, O]
  final case class Abstraction[S, V, O](vs: List[(V, S)], t: LN[S, V, O])
      extends LN[S, V, O]
  final case class Application[S, V, O](o: O, args: List[LN[S, V, O]])
      extends LN[S, V, O]

  implicit def TermAbt[S, V, O]: Abt[S, V, O, LN[S, V, O]] =
    new Abt[S, V, O, LN[S, V, O]] {
      def check[M[_, _]](view: View[V, O, LN[S, V, O]], valence: Valence[S])
                        (implicit ME: MonadError[M, E],
                                  MV: MonadVar[M[AbtError, ?], V],
                                  SE: Equal[S])
                        : M[AbtError, LN[S, V, O]] = {

        val Valence(sorts, sigma) = valence

        view match {
          case Var(x) =>
            if (sorts.isEmpty)
              FreeVar(x, sigma).point[M]
            else
              expectedNoBindings(valence).raiseError

          case Abs(xs, e) =>
            infer[M[AbtError, ?]](e) flatMap { case (Valence(_, tau), _) =>
              if (sigma === tau)
                Abstraction()
              else

            }

          case App(theta, es) =>
        }
      }

      def infer[M[_]](t: T)(implicit MV: MonadVar[M, V])
                     : M[(Valence[S], View[V, O, T])]
    }
}
