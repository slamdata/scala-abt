package abt

import scala.Predef.String

// TODO: Finish cases
sealed trait AbtError[S, V]

object AbtError {
  final case class ExpectedNoBindings[S, V](v: Valence[S]) extends AbtError[S, V]
  final case class SortMismatch[S, V](s1: S, s2: S) extends AbtError[S, V]
  final case class ValenceMismatch[S, V](v1: Valence[S], v2: Valence[S]) extends AbtError[S, V]

  def expectedNoBindings[S, V]: Valence[S] => AbtError[S, V] = ExpectedNoBindings(_)
  def sortMismatch[S, V]: (S, S) => AbtError[S, V] = SortMismatch(_, _)
  def valenceMismatch[S, V]: (Valence[S], Valence[S]) => AbtError[S, V] = ValenceMismatch(_, _)
}

