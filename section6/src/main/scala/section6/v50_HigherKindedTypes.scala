package section6

import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global

object v50_HigherKindedTypes {

  // def multiply[A, B](listA: List[A], listB: List[B]): List[(A, B)] =
  //   for
  //     a <- listA
  //     b <- listB
  //   yield a -> b

  // def multiply[A, B](optionA: Option[A], optionB: Option[B]): Option[(A, B)] =
  //   for
  //     a <- optionA
  //     b <- optionB
  //   yield a -> b

  // def multiply[F[_], A, B](fa: F[A], fb: F[B]): F[(A, B)] =
  //   for
  //     a <- fa
  //     b <- fb
  //   yield a -> b


  trait Monad[F[_], A]:
    def flatMap[B](f: A => F[B]): F[B]
    def map[B](f: A => B): F[B]

  implicit class MonadList[A](list: List[A]) extends Monad[List, A]:
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)
    override def map[B](f: A => B): List[B] = list.map(f)

  implicit class MonadOption[A](option: Option[A]) extends Monad[Option, A]:
    override def flatMap[B](f: A => Option[B]): Option[B] = option.flatMap(f)
    override def map[B](f: A => B): Option[B] = option.map(f)

  // external API
  def multiply[F[_], A, B](implicit ma: Monad[F, A], mb: Monad[F, B]): F[(A, B)] =
    for
      a <- ma
      b <- mb
    yield a -> b


  // Scala 3 version
  trait Monad2[F[_], A]:
    def flatMap[B](f: A => F[B]): F[B]
    def map[B](f: A => B): F[B]

  given listToMonad[A]: Conversion[List[A], Monad2[List, A]] with
    def apply(list: List[A]): Monad2[List, A] = new Monad2[List, A] {
      override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)
      override def map[B](f: A => B): List[B] = list.map(f)
    }

  given optionToMonad[A]: Conversion[Option[A], Monad2[Option, A]] with
    def apply(option: Option[A]): Monad2[Option, A] = new Monad2[Option, A] {
      override def flatMap[B](f: A => Option[B]): Option[B] = option.flatMap(f)
      override def map[B](f: A => B): Option[B] = option.map(f)
    }

  // external API
  def multiply2[F[_], A, B](ma: Monad2[F, A], mb: Monad2[F, B]): F[(A, B)] =
    for
      a <- ma
      b <- mb
    yield a -> b
  

  def part1() =
    println("V50: hello part1")

    trait AHigherKindedType[F[_]]  
      
    val monadList = new MonadList(List(1,2,3,4))
    println(monadList.flatMap(x => List(x, x*2)))
    println(monadList.map(_ + 2))
    // println(multiply(MonadList(List(1,2,3)), MonadList(List("A","B","C"))))
    println(multiply(List(1,2,3), List("A","B","C")))
    println(multiply(Option(42), Option("ABC")))
    println(multiply(Option(42), Option(null)))
    // println(multiply(MonadOption(Option(42)), MonadOption(Option("ABC"))))
    // println(multiply(MonadOption(Option(42)), MonadOption(Option(null))))
    
    println(s"multiply2(List(1,2,3), List(\"A\",\"B\",\"C\"): ${multiply2(List(1,2,3), List("A","B","C"))}")
    println(s"multiply2(Option(42), Option(\"ABC\")): ${multiply2(Option(42), Option("ABC"))}")
    println(s"multiply2(Option(42), Option(null)): ${multiply2(Option(42), Option(null))}")
    println(multiply2(Option(42), Option(null)))
    val test: Monad2[Option, Int] = Option(42)  // given conversion transforms Option to Monad2[Option, A]
    test.map(x => x) match
      case Some(x) => println(s"Option(x): $x")
      case None => println(s"Option(x): None")
    

  @main def mainV50 =
    part1()
}
