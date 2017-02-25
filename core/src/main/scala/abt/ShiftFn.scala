package abt

import scala.AnyVal
import scalaz._

final case class ShiftFn[A, B](f: (Coord, A) => B) extends AnyVal

object ShiftFn {
  implicit val shiftFnCat: Category[ShiftFn] =
    new Category[ShiftFn] {
      def id[A] =
        ShiftFn((_, a) => a)

      def compose[A, B, C](f: ShiftFn[B, C], g: ShiftFn[A, B]): ShiftFn[A, C] =
        ShiftFn((c, a) => f.f(c, g.f(c.shiftDown, a)))
    }
}
