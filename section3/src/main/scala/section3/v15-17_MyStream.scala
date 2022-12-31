package section3

import scala.annotation.tailrec

/*
  use: 
  naturals = MyStream.from(1)(x => x + 1) -- gives the stream of natural numbers
  naturals.take(100)  // lazily evaluated stream of the first 100 naturals
  naturals.take(100).foreach(println) // fine, since finite stream
  naturals.foreach(println)   // will crash -- it's infinite
  naturals.map(_ * 2) // stream of all even numbers
*/
abstract class MyStream[+A]:
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](el: B): MyStream[B] // prepend operator
  def ++[B >: A](otherStream: => MyStream[B]): MyStream[B]  // concatenation operator

  def foreach(f: A => Unit): Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]): MyStream[B]
  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes the first n elements of this stream
  def takeList(n: Int): List[A] = take(n).toList()

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] = 
    if isEmpty then acc.reverse
    else tail.toList(head :: acc)
end MyStream

object MyStream:
  def from[A](start: A)(generator: A => A): MyStream[A] = 
    NonEmptyStream(start, MyStream.from(generator(start))(generator))

end MyStream

object EmptyStream extends MyStream[Nothing]:   // extends Nothing, since A is covariant
  def isEmpty: Boolean = true
  def head: Nothing = throw new NoSuchElementException
  def tail: MyStream[Nothing] = throw new NoSuchElementException

  def #::[B >: Nothing](el: B): MyStream[B] = NonEmptyStream(el, this) // prepend operator
  def ++[B >: Nothing](otherStream: => MyStream[B]): MyStream[B] = otherStream  // concatenation operator

  def foreach(f: Nothing => Unit): Unit = ()
  def map[B](f: Nothing => B): MyStream[B] = this
  def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this
  def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  def take(n: Int): MyStream[Nothing] = this // takes the first n elements of this stream
end EmptyStream

//  Tail needs to be call by name, or won't be able to handle infinite streams
class NonEmptyStream[+A](h: A, t: => MyStream[A]) extends MyStream[A]:
  def isEmpty: Boolean = false
  override val head: A = h
  override lazy val tail: MyStream[A] = t   // this is "call by need"

  /* #:: -- prepend operator
    val s = NonEmptyStream(1, EmptyStream)  // EmptyStream is lazy - call by need
    val prepended = 1 #:: s => NonEmptyStream(1, s)  --> preserves lazy evaluation - s is lazily evaluated, so s.tail (EmptyStream) is still not evaluated either
  */
  def #::[B >: A](el: B): MyStream[B] = NonEmptyStream(el, this)

  /*  ++ -- concatenation operator
    works similarly to #:: - lazy evaluation is preserved 
  */
  def ++[B >: A](otherStream: => MyStream[B]): MyStream[B] = NonEmptyStream(head, tail ++ otherStream)

  def foreach(f: A => Unit): Unit = 
    f(head)
    tail.foreach(f)
  def map[B](f: A => B): MyStream[B] = NonEmptyStream(f(head), tail.map(f)) // preserves lazy evaluation
  def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f) // preserves lazy evaluation
  def filter(predicate: A => Boolean): MyStream[A] = 
    if (predicate(head)) then NonEmptyStream(head, tail.filter(predicate))
    else tail.filter(predicate)

  def take(n: Int): MyStream[A] = 
    if n <= 0 then EmptyStream
    else if n == 1 then NonEmptyStream(head, EmptyStream)
    else NonEmptyStream(head, tail.take(n - 1))
end NonEmptyStream



object MyStreamTest extends App:
  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.head)
  println(naturals.tail.head)  // triggers evaluation of tail (from lazy tail value)
  println(naturals.tail.tail.head)  // triggers evaluation of tail (from lazy tail value)

  val startFrom0 = 0 #:: naturals   // naturals.#::(0)
  println(startFrom0.head)

  // startFrom0.take(10000).foreach(println)

  // map, flatMap
  println(startFrom0.map(_ * 2).take(20).toList())
  println(startFrom0.flatMap(x => NonEmptyStream(x, NonEmptyStream(x + 1, EmptyStream))).take(10).toList())
  println(startFrom0.filter(_ < 10).take(10).toList())

  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] =
    NonEmptyStream(first, fibonacci(second, first + second))

  println(fibonacci(1,1).take(100).toList())

  def primes(numbers: MyStream[Int]): MyStream[Int] =
    if numbers.isEmpty then numbers
    else NonEmptyStream(numbers.head, primes(numbers.tail.filter(n => n % numbers.head != 0)))

  println(primes(MyStream.from(2)(_ + 1).take(100)).toList())
end MyStreamTest