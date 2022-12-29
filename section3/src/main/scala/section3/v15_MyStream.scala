package section3

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
  def ++[B >: A](el: B): MyStream[B]  // concatenation operator

  def foreach(f: A => Unit): Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]): MyStream[B]
  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes the first n elements of this stream
  def takeList(n: Int): List[A]
end MyStream

object MyStream:
  def from[A](start: A)(generator: A => A): MyStream[A] = ???
end MyStream

object EmptyStream extends MyStream[Nothing]:   // extends Nothing, since A is covariant
  def isEmpty: Boolean = true
  def head: Nothing = ???
  def tail: MyStream[Nothing] = ???

  def #::[B >: Nothing](el: B): MyStream[B] = ??? // prepend operator
  def ++[B >: Nothing](el: B): MyStream[B] = ???  // concatenation operator

  def foreach(f: Nothing => Unit): Unit = ???
  def map[B](f: Nothing => B): MyStream[B] = ???
  def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = ???
  def filter(predicate: Nothing => Boolean): MyStream[A] = ???

  def take(n: Int): MyStream[Nothing] = ??? // takes the first n elements of this stream
  def takeList(n: Int): List[Nothing] = ???
end EmptyStream

class NonEmptyStream[+A] extends MyStream[+A]
  def isEmpty: Boolean = ???
  def head: A = ???
  def tail: MyStream[A] = ???

  def #::[B >: A](el: B): MyStream[B] = ??? // prepend operator
  def ++[B >: A](el: B): MyStream[B] = ???  // concatenation operator

  def foreach(f: A => Unit): Unit = ???
  def map[B](f: A => B): MyStream[B] = ???
  def flatMap[B](f: A => MyStream[B]): MyStream[B] = ???
  def filter(predicate: A => Boolean): MyStream[A] = ???

  def take(n: Int): MyStream[A] = ??? // takes the first n elements of this stream
  def takeList(n: Int): List[A] = ???
end NonEmptyStream



object MyStreamTest extends App:

end MyStreamTest