package section3

object v15_LazyEvaluation:
  def part1 = 
    println("----- Part 1 -----")

    // lazy DELAYS the evaluatoin of the value
    lazy val x: Int = throw new RuntimeException  // will not evaluate until use, then evaluates once
    // println(x)  // if x is not used, it is not evaluated - so, no exception

    lazy val y: Int = {
      println("hello from lazy eval for y")
      42
    }
    println(s"y: $y")  // y is evaluated (one and only evaluation for y)
    println(s"y again: $y") // y has been evaluated, so not evaluated again

    // examples of implications
    //    1. side effects
    def sideEffectCondition: Boolean = 
      println("Boo")
      true
    def simpleCondition: Boolean = false
    lazy val lazyCondiion = sideEffectCondition
    println(if simpleCondition && lazyCondiion then "yes" else "no") // short-circuits and doesn't evaluate lazyCondition
    println(if lazyCondiion && simpleCondition then "yes" else "no") // evaluates lazyCondition

    //    2. in conjuction with call by name
    def byNameMethod(n: => Int): Int = n + n + n + 1
    def retrieveMagicValue() = {
      println("waiting...")
      Thread.sleep(1000)  // long computation
      42
    }
    println(byNameMethod(retrieveMagicValue()))
    
    // improve by using lazy val: technique is refered to as "CALL BY NEED"
    def byNameMethod2(n: => Int): Int =
      lazy val t = n  // n is only evaluated once, using "CALL BY NEED"
      t + t + t + 1
    println(byNameMethod2(retrieveMagicValue()))

    //    3. filtering with lazy values
    def lessThan30(i: Int): Boolean =
      println(s"$i is less than 30?")
      i < 30
    def moreThan20(i: Int): Boolean =
      println(s"$i is more than 20?")
      i > 20
    val numbers = List(1, 25, 40, 5, 23)
    val lt30 = numbers.filter(lessThan30)
    val gt20 = lt30.filter(moreThan20)
    println(gt20)

    val lt30Lazy = numbers.withFilter(lessThan30) // withFilter uses lazy vals under the hood
    val gt20Lazy = lt30Lazy.withFilter(moreThan20)
    println()
    println(gt20Lazy)
    gt20Lazy.foreach(println)
    println()

    // for-comprehensoins use withFilter with guards
    val for1 = for
      a <- List(1,2,3) if a % 2 == 0
    yield a + 1
    val for1Equivalent = List(1,2,3).withFilter(_ % 2 == 0).map(_ + 1)

  end part1

  def exercise1 =
    
  end exercise1

end v15_LazyEvaluation


@main def v15_LazyEvaluationMain =
  println("---------- Section 3: Lazy Evaluation ----------")
  
  // v15_LazyEvaluation.part1
  v15_LazyEvaluation.exercise1
  
  println("\n---------- Section 3: Lazy Evaluation ----------")  