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

// TODO: Finish cases
sealed trait AbtError[S, V]

object AbtError {
  final case class ExpectedNoBindings[S, V](v: Valence[S]) extends AbtError[S, V]
  final case class SortMismatch[S, V](s1: S, s2: S) extends AbtError[S, V]
  final case class ValenceMismatch[S, V](v1: Valence[S], v2: Valence[S]) extends AbtError[S, V]
  final case class UnequalLengths[S, V](xs: String, ys: String) extends AbtError[S, V]
  final case class UnexpectedBoundVariable[S, V](s: S) extends AbtError[S, V]

  def expectedNoBindings[S, V]: Valence[S] => AbtError[S, V] =
    ExpectedNoBindings(_)

  def sortMismatch[S, V]: (S, S) => AbtError[S, V] =
    SortMismatch(_, _)

  def valenceMismatch[S, V]: (Valence[S], Valence[S]) => AbtError[S, V] =
    ValenceMismatch(_, _)

  def unequalLengths[S, V]: (String, String) => AbtError[S, V] =
    UnequalLengths(_, _)

  def unexpectedBoundVariable[S, V]: S => AbtError[S, V] =
    UnexpectedBoundVariable(_)
}

