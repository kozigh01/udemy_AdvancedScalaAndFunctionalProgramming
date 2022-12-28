package section3.v10

import scala.annotation.tailrec

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
  def +(el: A): MySet[A]
  def ++(otherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(pred: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

class EmptySet[A] extends MySet[A]:
  override def contains(el: A): Boolean = false
  override def +(el: A): MySet[A] = NonEmptySet[A](el, this)
  override def ++(otherSet: MySet[A]): MySet[A] = otherSet

  override def map[B](f: A => B): MySet[B] = EmptySet[B]
  override def flatMap[B](f: A => MySet[B]): MySet[B] = EmptySet[B]
  override def filter(pred: A => Boolean): MySet[A] = this
  override def foreach(f: A => Unit): Unit = ()

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

object MySetTests extends App:
  val set1 = MySet(1,2,3,4)
  set1 + 5 + 5 foreach { x => print(s" $x") }; println
  set1 + 5 ++ MySet(-1, -2) foreach { x => print(s" $x") }; println
  set1 + 5 ++ MySet(-1, -2) map (x => x * 10) foreach { x => print(s" $x") }; println
  set1 + 5 ++ MySet(-1, -2) flatMap (x => MySet(x, x * 10)) foreach { x => print(s" $x") }; println
  set1 + 5 ++ MySet(-1, -2) flatMap (x => MySet(x, x * 10)) filter (_ % 2 == 0) foreach { x => print(s" $x") }; println


end MySetTests