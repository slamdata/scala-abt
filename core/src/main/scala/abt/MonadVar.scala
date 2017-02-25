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
import scala.Predef.implicitly
import scalaz._

trait MonadVar[F[_], A] extends Monad[F] {
  /** Generates a fresh variable tagged with a name. */
  def named(name: String): F[A]

  /** Clones the given variable.
    *
    * TODO: Better description.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Overloading"))
  def clone(a: A): F[A]
}

object MonadVar {
  def apply[F[_], A](implicit FA: MonadVar[F, A]): MonadVar[F, A] = FA

  implicit def eitherTMonadVar[F[_], E, A](implicit F: MonadVar[F, A]): MonadVar[EitherT[F, E, ?], A] =
    new MonadVar[EitherT[F, E, ?], A] {
      type M[X] = EitherT[F, E, X]
      private val M: Monad[M] = implicitly
      def named(name: String): EitherT[F, E, A] = EitherT.right(F.named(name))
      @SuppressWarnings(Array("org.wartremover.warts.Overloading"))
      def clone(a: A): EitherT[F, E, A] = EitherT.right(F.clone(a))
      def point[B](b: => B): EitherT[F, E, B] = M.point(b)
      def bind[B, C](fa: EitherT[F, E, B])(f: B => EitherT[F, E, C]): EitherT[F, E, C] = M.bind(fa)(f)
    }
}
