package com.mkozi.section2

import scala.annotation.tailrec

object v7_AdvancedPatternMatching2 {
  def part1 =
      println("----- Part 1 -----")

      // infix patterns
      List(1) match
        case head :: Nil => println(s"the only elemet is $head")
        case _ => print("no match")

      case class Or[A, B](a: A, b: B)
      val either = Or(2, "two")
      val result1 = either match
        // case Or(number, str) => s"$number is written as $str"   // typical syntax
        case number Or str => s"$number is written as $str"   // infix syntax
        case _ => "no match"
      println(s"either result1: $result1")
      
      // decomposing sequences
      val result2 = List(1,2,3,4) match
        case List(1, _*) => "starts with 1"   // using the vararg _*

      // unapply sequence
      abstract class MyList[+A]:
        def head: A = ???
        def tail: MyList[A] = ???
      case object Empty extends MyList[Nothing]
      case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]
      object MyList:
        // @tailrec
        def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
          if list == Empty then Option(Seq.empty)
          else unapplySeq(list.tail).map(x => list.head +: x)

      val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
      val decomposed = myList match
        case MyList(1,2,_*) => "starting with 1, 2"
        case _ => "myList not matched"
      println(s"myList matching: $decomposed")
      

      // custom return types for unapply
      //    return from custom matcher requires 2 methods: isEmpty: Boolean, get: something
      case class Person(name: String, age: Int)
      abstract class Wrapper[T]:
        def isEmpty: Boolean
        def get: T

      object PersonWrapper:
        def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
          def isEmpty: Boolean = false
          def get: String = person.name
        }

      val bobby = Person("Bobby", 33)
      val match1 = bobby match
        case PersonWrapper(name @ "Bobby") => println(s"matched PersonWrapper with specific name '$name'")
        case PersonWrapper(name) => println(s"matched PersonWrapper with and name: '$name'")
        case _ => println("didn't match anything")
    
}

@main def v6_AdvancedPatternMatching2Main =
  println("---------- Section 2: Advanced Pattern Matching 2 ----------")
  
  v7_AdvancedPatternMatching2.part1
  // v7_AdvancedPatternMatching2.exercise1
  
  println("\n---------- Section 2: Advanced Pattern Matching 2 ----------")  