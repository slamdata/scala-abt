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

import scalaz._

final case class ShiftFn[A, B](f: (Coord, A) => B) extends AnyVal

object ShiftFn {
  implicit val shiftFnCat: Category[ShiftFn] =
    new Category[ShiftFn] {
      def id[A]: ShiftFn[A, A] =
        ShiftFn((_, a) => a)

      def compose[A, B, C](f: ShiftFn[B, C], g: ShiftFn[A, B]): ShiftFn[A, C] =
        ShiftFn((c, a) => f.f(c, g.f(c.shiftDown, a)))
    }
}
