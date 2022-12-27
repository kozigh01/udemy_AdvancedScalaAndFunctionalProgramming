package com.mkozi.section2

import scala.annotation.tailrec

object V4_RecapScalaBasics:
  
  def part1 =
    println("----- Part 1 -----")

    val bool1: Boolean = false
    val condition1 = if bool1 then 42 else 65

    val codeBlock1 = {
      if bool1 then 54 else 52
      56
    }

    // Unit - result of a side-effect (like void in other languages)
    val unit1 = println("Hello, scala")

    // functions
    def func1(x: Int): Int = x + 1

    // recursion: stack or tail
    @tailrec def factorial(n: Int, accum: Int = 1): Int =
      if n <= 0 then accum 
      else factorial(n-1, n * accum)
    println(s"factorial(6): ${factorial(6)}")

    // object oriented programming
    class Animal
    class Dog extends Animal
    val dog1: Animal = new Dog  // subtyping polymorphism

    trait Carnivore:
      def eat(a: Animal): Unit
    
    class Crocodile extends Animal, Carnivore:
      override def eat(a: Animal): Unit = println("crunch!")

    // method notations
    val croc1 = Crocodile()
    croc1.eat(dog1)
    croc1 eat dog1  // infix notation

    // operators are really methods in scala
    val x1 = 3 + 4
    val x2 = 3.*(4) // equivalent to 3 + 4

    // anonomous classes
    val carnivore1 = new Carnivore {
      override def eat(a: Animal): Unit = println("anonomous carnivore: chomp!")
    }
    carnivore1.eat(dog1)

    // generics
    abstract class MyList[+T] // variance and variance problems in THIS course
    // singleton objects and companions
    object MyList:
      def apply[T](): T = ??? // apply can be called like a function myList1()
      def genericMethod[T](): T = ???

    // case classes - includes auto-generaated companions with a lot of boilerplate features
    case class Person(name: String, age: Int)

    // exceptons and try/catch/final
    // val throws1 = throw new RuntimeException("throwing an exception")   // returns Nothing
    val try1 = try
      throw new RuntimeException("booo!!")
    catch
      case e: RuntimeException => println("caught a RuntimeException")
      case e: Exception => println("caught ab Exception")
    finally
      println("will always run this, even if there is an error")

  def part2 =
    println("----- Part 2 -----")

    // functional programming
    // functions are actually instances of classes with apply() methods
    val incrementer = new Function1[Int, Int] {
      override def apply(v1: Int): Int = v1 + 3
    }
    val incr1: Int = incrementer(5)
    println(s"incr1: $incr1")

    // function definition syntactic sugar
    val incr2: Function1[Int, Int] = (inc: Int) => inc + 1
    val incr3: Int => Int = (inc: Int) => inc + 1   // support for lambda syntax
    val incr4: Int => Int = _ + 1   // even shorter function definition syntax
    println(s"incr4(3): ${incr4(3)}")
    println(s"List(1,2,3).map(incr4): ${List(1,2,3).map(incr4)}")

    // map, flatMap, Filter -> for comprehensions
    val suits = List("Heart", "Diamond", "Spade", "Club")
    val ranks = List(1,2,3,4)
    val pairs1 = for
      s <- suits
      r <- ranks
    yield s"$s$r"
    println(s"pairs1: $pairs1")
    // pairs equivalent to:
    val pairs2 = suits.flatMap(s => ranks.map(r => s"$s$r"))
    println(s"pairs2: $pairs2")
    // with filter
    val pairs3 = for
      s <- suits
      r <- ranks if r % 2 == 0
    yield s"$r$s"
    println(s"pairs3: $pairs3")
    val pairs4 = suits.flatMap(s => ranks.filter(_ % 2 == 1).map(r => s"$r$s"))
    println(s"pairs4: $pairs4")

    // scala collections: Seq, Arrays, Lists, Vectors, Maps, Tuples
    val map1 = Map("Daniel"  -> 789, ("Mark", 321))

    // "collections" (more like abstract computations): Option, Try, Either
    val opt1 = Option("42")
    val opt2: Option[String] = Option(null)
    println(s"opt1, opt2: $opt1, $opt2")
    println(s"opt1.flatMap(...): ${opt1.map((s: String) => List(s, s+"1"))}")
    println(s"opt1.map(...): ${opt1.flatMap((s: String) => List(s, s+"1"))}")
    println(s"opt1.filter(...): ${opt1.filter((s: String) => s.length() <= 10)}")
    println(s"opt1.filter(...): ${opt1.filter((s: String) => s.length() > 10)}")
    println(s"opt2.map(...): ${opt2.map((n: String) => List(n, n+"1"))}")
    println(s"opt2.flatMap(...): ${opt2.flatMap((n: String) => List(n, n+"1"))}")

    // pattern matching -- see the basics course


end V4_RecapScalaBasics


@main def v4Main =
  println("---------- Section 2: Recap Scala Basics ----------")
  
  // V4_RecapScalaBasics.part1
  V4_RecapScalaBasics.part2
  
  println("\n---------- Section 2: Recap Scala Basics ----------")  
