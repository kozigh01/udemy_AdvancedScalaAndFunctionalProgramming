package section2

import scala.util.Try

object v5_DarkSyntaxSugar {
  def part1 = 
    println("----- Part 1 -----")

    // 1. methods with single parameter - can supply code block
    def method1(arg: Int): String = s"your arg = $arg"
    val result1a = method1 { 42 }
    val result1b = method1 {
      // more code can go here in the code block
      println(s"in the code block for parameter 'arg'") 
      // more code can go here in the code block
      42  // value of last expression in code block is the value of the argument
    }
    println(s"result1b: $result1b")

    val try1 = Try {    // invokig the apply method from Try with alt curly brace syntax
      // code to wrap in the Try
      val a = 11
      val b = 0
      a / b
    }
    println(s"try1: $try1")

    List(1,2,3).map { x => 
      x + 1
    }

    // 2. single abstract method: instances of traits with single method can be reduced to a lambda
    trait Action:
      def Act(x: Int): Int
    
    // normal uses: extend trait, use anonymous class
    class ActionItem1 extends Action:
      override def Act(x: Int): Int =
        println(s"extending Action: $x")
        x + 1            
    val action1a = ActionItem1()
    action1a.Act(42)

    val action1b = new Action {
      override def Act(x: Int): Int = 
        println(s"anonymous Action: $x")
        x + 2
    }
    action1b.Act(42)

    // syntax sugar: single abstract method
    val action1c: Action = (x: Int) => x * 2    // implements single method with the lambda
    println(s"action1c(42): ${action1c.Act(42)}")

    def useAction(n: Int, action: Action) = println(s"using the Action: ${action.Act(n)}")
    useAction(42, _ + 3)

    // example: Runnable (concurrency)
    val thread1a = Thread(new Runnable {  // anonymous class syntax
      override def run(): Unit = println("Hello, Scala")
    })
    val thread1b = Thread(() => println("Hell, Scala - using lambda"))

    // syntax sugar #2 can also work for abstract class with only one unimplemented method
    abstract class AnAbstractType:
      def someMethod1(x: Int): Int = 23
      def someMethod2(): Unit = println("doing something")
      def f(a: Int): String

    val aat1: AnAbstractType = (n: Int) => s"implementing the method f: n = $n"
    println(s"aat1.f(7): ${aat1.f(7)}")
  
  end part1
  
  def part2 = 
    println("----- Part 2 -----")
    
    // 3. the :: and the #:: methods are special

    // normally, following would be equivalent: 2.::(List(3,4))
    //    however, :: is different and equivalent to List(3,4).::(2)
    //  scala specification: last character determines associativity:
    //    if ends in colon (:) then right associative, otherwise left associative
    val prependedList = 2 :: List(3,4)   // equivalent to List(3,4).::(2)
    1 :: 2 :: 3 :: List(4,5) // means: ((List(4,5).::(3)).::(2)).::(1)

    class MyStream[T]:
      def -->:(value: T): MyStream[T] = this   // ends with ':', so right associative

    // myStream equivalent to: MyStream[Int].-->:(3).-->:(2).-->:(1) (is right associative)
    val myStream1 = 1 -->: 2 -->: 3  -->: MyStream[Int] 
    val myStream2 = MyStream[Int].-->:(3).-->:(2).-->:(1)

    // 4. multi-word method naming
    class Teen(name: String):
      def `and then said`(msg: String) = println(s"$name said $msg")
    val bob = Teen("bob")
    bob `and then said` "Scola rocks!"  // infix notation with multi-word method name
    bob.`and then said`("Whoa!")

    // 5. infix types for generics
    class Composite[A,B]
    val composite1: Composite[Int, String] = Composite[Int, String]()
    val composite2: Int Composite String = Composite[Int, String]()  // type using infix notation

    // better example
    class -->[A,B]
    val towards: Int --> String = -->[Int,String]()

    // 6. update method -- is special (like apply())
    //    very used in mutable collections
    val array1 = Array(1,2,3)
    array1(2) = 7   // compiler rewrites to: array1.update(2, 7)

    // 7. setters for mutable containers
    class Mutable:
      private var internalMember: Int = 0   // private for object oriented encapsulation
      // getter
      def member: Int = internalMember
      // setter
      def member_=(value: Int): Unit = internalMember = value


    val mutable1 = Mutable()
    mutable1.member = 42  // compiler rewrites to: mutable1.member_=(42)

  end part2

  def part3 = 
    println("----- Part 3 -----")
  end part3
}


@main def v5_DarkSyntaxSugarMain =
  println("---------- Section 2: Dark Syntax Sugar ----------")
  
  // v5_DarkSyntaxSugar.part1
  v5_DarkSyntaxSugar.part2
  
  println("\n---------- Section 2: Dark Syntax Sugar ----------")  