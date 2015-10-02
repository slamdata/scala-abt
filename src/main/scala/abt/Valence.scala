package abt

/** Describes the sort of an argument to an operator along with the number
  * and sorts of the variables bound within it.
  */
final case class Valence[Sort](vars: List[Sort], arg: Sort)