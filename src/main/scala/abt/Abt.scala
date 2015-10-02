package abt

import scalaz._, Scalaz._

/** @tparam V Type of variables
  * @tparam O underlying AST
  * @tparam T Abt concrete instance
  */
trait Abt[S, V, O, T] {
  import View._
  def check[M](view: View[V, O, T], valence: Valence[S])(implicit M: MonadVar[V, ?], S: Equal[S]): EitherT[M, AbtError, T]
  def infer[M: MonadVar[V, ?]](t: T): M[(Valence[S], View[V, O, T])]
}

object Abt {
  def apply[S, V, O, T](implicit A: Abt[S, V, O, T]): Abt[S, V, O, T] = A
}
