package section3

import scala.util.Try

object v9_PartialFunctions:
  
  def part1 =
    println("----- Part 1 -----")

    // function over the whole Int domain - can pass any Int as argument
    val func1 = (x: Int) => x + 1 // Function1[Int, Int] === Int -> Int

    // Partial Function: want to reduce the domain of the arguments
    //    1. crude implementation
    class FunctionNotApplicableException(msg: String) extends RuntimeException(msg)
    val func2 = (x: Int) =>
      if x == 1 then 42
      else if x == 2 then 56
      else if x == 5 then 999
      else throw new FunctionNotApplicableException("boom")
    val result1 = func2(5)

    //    2. a bit nicer function: domain is (1,2,5) => Int (partial function from int to int)
    val func3 = (x: Int) =>
      x match
        case 1 => 42
        case 2 => 56
        case 5 => 999
    val result2 = func3(5)

    //    3. scala supports a partial function syntax based on cases
    val partialFunc1: PartialFunction[Int, Int] = {  // a partial function value (partial function literal)
        case 1 => 42
        case 2 => 56
        case 5 => 999
    }
    println(s"partialFunc1(2): ${partialFunc1(2)}")
    println(s"partialFunc1(11): ${Try(partialFunc1(11))}")  //  throws MatchError

    // partial function utilities
    //    isDefinedAt
    println(s"partialFunc1.isDefinedAt(5): ${partialFunc1.isDefinedAt(5)}")
    println(s"partialFunc1.isDefinedAt(11): ${partialFunc1.isDefinedAt(11)}")
      
    //    lifted: partial function can be "lifted" to a total function
    val liftedFunc1 = partialFunc1.lift   // converts to a total function: Int => Option[Int]
    println(s"liftedFunc1(2): ${liftedFunc1(2)}")
    println(s"liftedFunc(11): ${liftedFunc1(11)}")

    //    orElse: chain partial functions
    val partialFunc2 = partialFunc1.orElse {
      case 45 => 67
    }
    println(s"partialFunc2(2): ${partialFunc2(2)}")
    println(s"partialFunc2(11): ${Try { partialFunc2(11)} }")
    println(s"partialFunc2(45): ${partialFunc2(45)}")

    //    partial functions extend normal functions
    //      so, existing higher order functions (HOF) accept partial functions also
    val totalFunc1: Int => Int = {
      case 1 => 99
    }
    println(s"totalFunc1(1): ${totalFunc1(1)}")
    println(s"totalFunc1(2): ${Try { totalFunc1(2) }}")
    println { 
      List(1,2,3).map {   // existing HOF (List.map) can accept a partial function
        case 1 => 42
        case 2 => 78
        case 3 => 1000
      } 
    }

  def exercise1 =
    // 1. construct a partial function instance from the partial function trait
    val partialFunc1 = new PartialFunction[Int, Int] {
      override def isDefinedAt(x: Int): Boolean = 
        x == 1 || x == 2 || x == 3
        // x match
        //   case 1 => true
        //   case 2 => true
        //   case 3 => true
        //   case _ => false
        
      override def apply(v1: Int): Int = 
        v1 match
          case 1 => 101
          case 2 => 102
          case 3 => 103
    }
    println(s"partialFunc1(1): ${partialFunc1(1)}")
    println(s"partialFunc1(3): ${partialFunc1(3)}")
    println(s"partialFunc1(9): ${Try { partialFunc1(9) }}")

    // 2. dumb chatbot as a partial function
    println("Enter something for the chatbot:")
    val input = scala.io.StdIn.readLine()
    val respondFunc: PartialFunction[String, String] = {
      case "Hi" => "Hello there, friend"
      case "Bye" => "Check you later, tater"
    }
    val response1 = Try { respondFunc(input) }
    response1.foreach(m => println(s"response: $m"))


end v9_PartialFunctions


@main def v9_PartialFunctionsMain =
  println("---------- Section 3: Partial Functions ----------")
  
  // v9_PartialFunctions.part1
  v9_PartialFunctions.exercise1
  
  println("\n---------- Section 3: Partial Functions ----------") 