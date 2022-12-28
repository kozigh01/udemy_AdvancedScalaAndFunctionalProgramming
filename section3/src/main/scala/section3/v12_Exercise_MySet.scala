package section3.v12

import scala.annotation.tailrec
import scala.collection.View.Empty

object MySet:
  def apply[A](values: A*) =
    @tailrec
    def buildSet(valSeq: Seq[A], accum: MySet[A]): MySet[A] =
      if valSeq.isEmpty then accum
      else buildSet(valSeq.tail, accum + valSeq.head)

    buildSet(values.toSeq, EmptySet[A])


trait MySet[A] extends (A => Boolean):
  def apply(el: A): Boolean = contains(el)

  def contains(el: A): Boolean
  def +(el: A): MySet[A]  // add element to set
  def ++(otherSet: MySet[A]): MySet[A]  // union

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(pred: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  def -(el: A): MySet[A]  // remove element from set
  def --(otherSet: MySet[A]): MySet[A] // difference
  def &(otherSet: MySet[A]): MySet[A] // intersection

  def unary_! : MySet[A]
end MySet

class EmptySet[A] extends MySet[A]:
  override def contains(el: A): Boolean = false
  override def +(el: A): MySet[A] = NonEmptySet[A](el, this)
  override def ++(otherSet: MySet[A]): MySet[A] = otherSet

  override def map[B](f: A => B): MySet[B] = EmptySet[B]
  override def flatMap[B](f: A => MySet[B]): MySet[B] = EmptySet[B]
  override def filter(pred: A => Boolean): MySet[A] = this
  override def foreach(f: A => Unit): Unit = ()

  def -(el: A): MySet[A] = this
  def --(otherSet: MySet[A]): MySet[A] = this
  def &(otherSet: MySet[A]): MySet[A] = this

  def unary_! : MySet[A] = AllInclusiveSet[A]
end EmptySet


// class AllInclusiveSet[A] extends MySet[A]:
//   def contains(el: A): Boolean = true
//   def +(el: A): MySet[A] = this
//   def ++(otherSet: MySet[A]): MySet[A] = this

//   def map[B](f: A => B): MySet[B] = ???
//   def flatMap[B](f: A => MySet[B]): MySet[B] = ???

//   def filter(pred: A => Boolean): MySet[A] = ???  // property-based set
//   def foreach(f: A => Unit): Unit = ???

//   def -(el: A): MySet[A] = ???
//   def --(otherSet: MySet[A]): MySet[A] = filter(!otherSet)
//   def &(otherSet: MySet[A]): MySet[A] = filter(otherSet)

//   def unary_! : MySet[A] = EmptySet()
// end AllInclusiveSet


class PropertyBasedSet[A](property: A => Boolean) extends MySet[A]:

end PropertyBasedSet


class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A]:
  // @tailrec
  def contains(el: A): Boolean = el == head || tail.contains(el)
    // tail match
    //   case _: EmptySet[?] => if el == head then true else false
    //   case s: NonEmptySet[?] => tail.contains(el)
  def +(el: A): MySet[A] = 
    if this contains el then this else NonEmptySet(el, this)
  // @tailrec
  def ++(otherSet: MySet[A]): MySet[A] =
    tail ++ (otherSet + head)

  // @tailrec
  def map[B](f: A => B): MySet[B] = 
    (tail map f) + f(head) 
  // @tailrec
  def flatMap[B](f: A => MySet[B]): MySet[B] =
    (tail flatMap f) ++ f(head)
  // @tailrec
  def filter(pred: A => Boolean): MySet[A] =
    val filteredTail = tail filter pred
    if pred(head) then filteredTail + head
    else filteredTail
  // @tailrec
  def foreach(f: A => Unit): Unit =
    f(head) 
    tail foreach f

  def -(el: A): MySet[A] = 
    if head == el then tail
    else (tail - el) + head
  def --(otherSet: MySet[A]): MySet[A] =
    filter(!otherSet)
  def &(otherSet: MySet[A]): MySet[A] =
    filter(otherSet)  // apply(x) === contains(x)
    // filter(x => otherSet.contains(x))

  def unary_! : MySet[A] = ???
end NonEmptySet


object MySetTests extends App:
  val set1 = MySet(1,2,3,4)
  set1 + 5 + 5 foreach { x => print(s" $x") }; println
  set1 + 5 ++ MySet(-1, -2) foreach { x => print(s" $x") }; println
  set1 + 5 ++ MySet(-1, -2) map (x => x * 10) foreach { x => print(s" $x") }; println
  set1 + 5 ++ MySet(-1, -2) flatMap (x => MySet(x, x * 10)) foreach { x => print(s" $x") }; println
  set1 + 5 ++ MySet(-1, -2) flatMap (x => MySet(x, x * 10)) filter (_ % 2 == 0) foreach { x => print(s" $x") }; println

  val set2 = MySet(1,2,3,4,5,6,7)
  (set1 - 1).foreach(x => print(s" $x")); println
  (set1 - 3).foreach(x => print(s" $x")); println
  (set1 - 4).foreach(x => print(s" $x")); println
  
  (set2 -- MySet(3,5)).foreach(x => print(s" $x")); println
  (set2 -- MySet(1,7)).foreach(x => print(s" $x")); println
  (set2 -- MySet(1,2,6,7)).foreach(x => print(s" $x")); println
  (set2 -- MySet(2,4,6)).foreach(x => print(s" $x")); println

  (set2 & MySet(2,5)).foreach(x => print(s" $x")); println



end MySetTests