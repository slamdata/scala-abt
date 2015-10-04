package abt

import scala.Predef.String

import scalaz._

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

  implicit def eitherTMonadVar[F[_], E, A](implicit F: MonadVar[F, A]): MonadVar[EitherT[F, E, ?], A] =
    new MonadVar[EitherT[F, E, ?], A] {
      def named(name: String) = EitherT.right(F.named(name))
      def clone(a: A) = EitherT.right(F.clone(a))
    }
}
