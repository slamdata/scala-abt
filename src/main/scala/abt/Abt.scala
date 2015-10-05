package abt

import scalaz._

/** @tparam S Sort (syntactic category)
  * @tparam V Type of variables
  * @tparam O underlying AST
  * @tparam T Abt concrete instance
  */
trait Abt[S, V, O, T] {
  def check[M[_, _]](view: View[V, O, T], valence: Valence[S])
                    (implicit ME: MonadError[M, AbtError[S, V]],
                              MV: MonadVar[M[AbtError[S, V], ?], V],
                              S: Equal[S])
                    : M[AbtError[S, V], T]

  def infer[M[_]](t: T)(implicit MV: MonadVar[M, V]): M[(Valence[S], View[V, O, T])]
}

object Abt {
  def apply[S, V, O, T](implicit A: Abt[S, V, O, T]): Abt[S, V, O, T] = A
}
