package com.mkozi.section2

object v6_AdvancedPatternMatching {
    def part1 = 
      println("----- Part 1 -----")

      /*
        Typical pattern matching situations:
          - constants
          - wildcards (the "_")
          - case classes
          - tuples
          - some special magic like "head :: Nil"

        99% of time these will sufice, however let's learn some more like custom pattern matching
       */

      val numbers = List(1)
      val descr1 = numbers match
        case head :: Nil => println(s"the only element is $head")
        case _ => println("no match")

      // for some reason, can't use case class and need regular class
      //    but want to do pattern matching
      //    - use companion class and include unapply function
      class Person(val name: String, val age: Int)
      object PersonPattern:
        def unapply(person: Person): Option[(String, Int)] = 
          if person.age < 21 then Option((person.name, person.age))
          else None
        def unapply(age: Int): Option[String] =
          Option(if age < 21 then "minor" else "major")
      val bob = Person("Bob", 35)
      val greeting1 = bob match
        case PersonPattern(n, a) => s"Hi, I'm $n and I am $a years old"
        case _ => "No match"
      println(s"bob: $greeting1")
      val alice = Person("Alice", 19)
      val greeting2 = alice match
        case PersonPattern(n, a) => s"Hi, I'm $n and I am $a years old"
        case _ => "No match"
      println(s"alice: $greeting2")
      val legalStatus1 = alice.age match
        case PersonPattern(status) => s"Based on my age, I am a '$status'"
        case _ => "status: no match"
      println(s"Alice legal status: $legalStatus1")
      

    end part1

    def part2 = 
      println("----- Part 2 -----")
    end part2

    def exercise1 =
      println("----- Exercise 1 -----")

      // using Option{T]
      object even:
        def unapply(n: Int): Option[Boolean] = 
          if n % 2 == 0 then Option(true)
          else None
      object odd:
        def unapply(n: Int): Option[Boolean] = 
          if n % 2 == 1 then Option(true)
          else None
      object singleDigit:
        def unapply(n: Int): Option[Boolean] =
          if n > -10 && n < 10 then Option(true)
          else None

      val n: Int = -9
      val match1 = n match
        case even(_) => s"an even number"
        case singleDigit(_) => "number is a single digit"
        case odd(_) => s"an odd number"
        case _ => "exercise 1: no match" 
      println(s"exercise 1 - match1: $match1")

      // alternate using just Boolean instead of Option[Boolean]
      object even2:
        def unapply(n: Int): Boolean = n % 2 == 0
      object odd2:
        def unapply(n: Int): Boolean = n % 2 == 1 || n % 2 == -1
      object singleDigit2:
        def unapply(n: Int): Boolean = n > -10 && n < 10

      val n2: Int = -91
      val match2 = n2 match
        case even2() => s"an even number"
        case singleDigit2() => "number is a single digit"
        case odd2() => s"an odd number"
        case _ => "exercise 1: no match" 
      println(s"exercise 1 - match2: $match2")
}


@main def v6_AdvancedPatternMatchingMain =
  println("---------- Section 2: Advanced Pattern Matching ----------")
  
  // v6_AdvancedPatternMatching.part1
  v6_AdvancedPatternMatching.exercise1
  // v6_AdvancedPatternMatching.part2
  
  println("\n---------- Section 2: Advanced Pattern Matching ----------")  