package abt

trait Coord[A] {
  def origin(): A
  def shiftDown(a: A): A
  def shiftRight(a: A): A
}

sealed trait Term[S, V, O]
final case class FreeVar[S, V, O](v: V, s: S) extends Term[S, V, O]
final case class BoundVar[S, V, O](v: Coord, s: S) extends Term[S, V, O]
final case class Abstraction[S, V, O](vs: List[(V, S)], t: Term[S, V, O])
    extends Term[S, V, O]
final case class Application[S, V, O](o: O, args: List[Term[S, V, O]])
    extends Term[S, V, O]

object Term {
  implicit def TermAbt[S, V, O]: Abt[S, V, O, Term[S, V, O]] =
    new Abt[S, V, O, Term[S, V, O]] {
      def check[M[_, _]](view: View[V, O, Term[S, V, O]], valence: Valence[S])(implicit ME: MonadError[M, E], MV: MonadVar[M[AbtError, _], V], S: Equal[S]):
          M[Term[S, V, O]] = {
        val Valence(sorts, sigma) = valence
        view match {
          case Vari(x) =>
            if (sorts.isEmpty)
              AbtError("sorts not empty").raiseError
            else
              FreeVar(x, sigma).point[M]
          case Abstraction(xs, e) =>
            infer(e).map { case (Valence(_, tau), _) =>
              if (sigma === tau)
                Abs()
            }
          case Application(theta, es) =>
        }
      }

                                                                           }
}
