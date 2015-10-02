package abt

import scala.Predef.String

import scalaz.Monad

trait MonadVar[F[_], A] extends Monad[F] {
  /** Generates a fresh variable tagged with a name. */
  def named(name: String): F[A]

  /** Clones the given variable.
    *
    * TODO: Better description.
    */
  def clone(a: A): F[A]
}

object MonadVar {
  def apply[F[_], A](implicit FA: MonadVar[F, A]): MonadVar[F, A] = FA
}

