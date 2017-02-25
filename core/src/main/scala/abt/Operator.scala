package abt

trait Operator[S, O] {
  def arity(op: O): Arity[S]
}

object Operator {
  def apply[S, O](implicit O: Operator[S, O]): Operator[S, O] = O
}
