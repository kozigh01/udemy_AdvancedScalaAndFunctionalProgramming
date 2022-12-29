package section3

object v14_CurryingPartiallyAppliedFunctions {
    def part1 = 
      println("----- Part 1 -----")

      // curried functions -- multiple parameter lists
    //    1. curried function
      val adderF1: Int => Int => Int = 
        x => y => x + y
      val adderF1_5 = adderF1(5)   // 5 => y => x + y --> y => 5 + y
      println(s"adderF1_5(3) (Int => Int): ${adderF1_5(3)}")
      println(s"adderF1(11)(7): ${adderF1(11)(7)}")

      //  2. curried method:  functions != methods (JVM limitation)
      //      methods are properties of instances of classes, and are
      //      not instances of FunctionXX traits -- so, to use a method
      //      as a value, it must be transformed (lifted) to a function value.
      //      This is also called ETA-EXPANSION
      def subF2(x: Int)(y: Int): Int = x - y  // method != function
      val subF2_7: Int => Int = subF2(7)    // lifted function: subF2(7) --> y => 7 - y)
      println(s"subF2(33)(11): ${subF2(33)(11)}")
      println(s"subF2_7(3): ${subF2_7(3)}")

      //  2a. listing example
      def inc1(x: Int): Int = x + 1   // a method (which is != function)
      List(1,2,3).map(inc1) // ETA-expansion: compiler transforms to a function --> x => inc1(x)

      // partial function applications
      val subF2_13 = subF2(13) _  // the "_" is required in Scala 2, but not in Scala 3, it forces ETA-expansion
      val subF2b_13 = subF2(13)(_)  // alt syntax (only needed for scala 2)
      println(s"subF2_13(2): ${subF2_13(2)}")

      def subF2_a(x: Int, y: Int): Int = x - y
      val subF2_a_13 = subF2_a(13, _: Int)  // alt syntax (only needed for scala 2): y => subF2_a(13, y)

    end part1

    def exercise1 =
      val simpleAddFunc = (x: Int, y: Int) => x + y
      def simpleAddMethod(x: Int, y: Int) = x + y
      val curriedAddFunc: Int => Int => Int = x => y => x + y

      // create variations of add7 from above
      def add7a1(y: Int) = simpleAddFunc(7, y)
      println(s"add7a1(6): ${add7a1(6)}")
      def add7a2(y: Int) = simpleAddMethod(7, y)
      println(s"add7a2(6): ${add7a2(6)}")
      def add7a3(y: Int) = curriedAddFunc(7)(y)
      println(s"add7a3(6): ${add7a3(6)}")

      // can use with all three options
      val add7b1 = y => simpleAddFunc(7, y)
      println(s"add7b1(6): ${add7b1(6)}")

      // can use with all three options      
      val add7c1: Int => Int => Int = x => y => simpleAddFunc(x, y)
      val add7c2 = add7c1(7)
      println(s"add7c2(6): ${add7c2(6)}")
     
      val add7d1 = curriedAddFunc(7)
      println(s"add7d1(6): ${add7d1(6)}")
      
      // can use "curried" method for both methods and functions
      val add7e1a: Int => Int => Int = simpleAddMethod.curried  // conversion to a curried function
      val add7e1b: Int => Int = add7e1a(7)
      println(s"add7e1b(6): ${add7e1b(6)}")
      
      val add7e2a: Int => Int = simpleAddFunc.curried(7)   // conversion to a curried function
      println(s"add7e2a(6): ${add7e2a(6)}")
    
    end exercise1

    def part2 =
      // underscores are powerful
      def concat1(a: String, b: String, c: String): String = a + b + c

      // partially apply anywhere in parameter list
      val insertName = concat1("Hello, I'm ", _: String, ", how are you")  // ETA-expansion: String => String
      val insertNameEquivalent: String => String = x => concat1("Hello, I'm ", x, ", how are you")  // ETA-expansion: String => String
      println(s"insertName(\"Mark\"): ${insertName("Mark")}")
      println(s"insertNameEquivalent(\"Mark\"): ${insertNameEquivalent("Mark")}")

      // can use multiple _
      val insertName2 = concat1("Hello, I'm ", _: String, _: String)  // ETA-expansion: String => String
      val insertNameEquivalent2: (String, String) => String = (x, y) => concat1("Hello, I'm ", x, y)  // ETA-expansion: String => String
      println(s"insertName2(\"Mark\", \" more ...\"): ${insertName2("Mark", " more ...")}")
      println(s"insertNameEquivalent2(\"Mark\", \" more ...\"): ${insertNameEquivalent2("Mark", " more ...")}")

    end part2

    def exercise2 =
      // 1.
      val nums = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)
      def processNums(nbrs: List[Double], formatter: Double => String) =
        nums.foreach(n => print(s" ${formatter(n)}"))
        println()
        
      def formatter1(format: String)(n: Double) = format.format(n)
      processNums(nums, formatter1("%4.2f"))
      processNums(nums, formatter1("%8.6f"))
      processNums(nums, formatter1("%14.12f"))

      val formatter2 = (format: String) => (n: Double) => format.format(n)
      processNums(nums, formatter2("%4.2f"))
      processNums(nums, formatter2("%8.6f"))
      processNums(nums, formatter2("%14.12f"))

      // 2.
      def byName(n: => Int): Int = n + 1
      def byFunction(f: () => Int) = f() + 1
      def method: Int = 42
      def paranMethod(): Int = 42

      val paf1: Int => Int => Int = x => y => x + y 
      val paf2: Int => Int = paf1(22) 

      println(s"byName(55): ${byName(55)}")
      println(s"byName(method): ${byName(method)}")
      println(s"byName(paranMethod()): ${byName(paranMethod())}")
      println(s"byName((() => 57)()): ${byName((() => 57)())}")
      println(s"byName(paf1(7)(3)): ${byName(paf1(7)(3))}")
      println(s"byName(paf2(3)): ${byName(paf2(3))}")

      // println(s"byFunction(method): ${byFunction(method)}")
      println(s"byFunction(paranMethod()): ${byFunction(paranMethod)}")
      println(s"byFunction(() => 57): ${byFunction(() => 57)}")
      println(s"byFunction(() => paf1(7)(3)): ${byFunction(() => paf1(7)(3))}")
      println(s"byFunction(() => paf2(3)): ${byFunction(() => paf2(3))}")


    end exercise2
}


@main def v6_AdvancedPatternMatchingMain =
  println("---------- Section 3: Currying and Partial Functon Application ----------")
  
  // v14_CurryingPartiallyAppliedFunctions.part1
  // v14_CurryingPartiallyAppliedFunctions.exercise1
  // v14_CurryingPartiallyAppliedFunctions.part2
  v14_CurryingPartiallyAppliedFunctions.exercise2
  
  println("\n---------- Section 3: Currying and Partial Functon Application ----------")  