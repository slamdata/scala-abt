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
