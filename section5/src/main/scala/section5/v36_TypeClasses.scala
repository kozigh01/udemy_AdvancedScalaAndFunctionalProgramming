package section5

object v36_TypeClasses {
  def part1() =
    // extend existing classes - implicit classes
    //  2.isPrime
    //  Scala 3 - see extension methods

    // implilcit classes must have exactly 1 argument
    //    called: type enrichment
    implicit class RichInt(value: Int) {
      def isEven: Boolean = value % 2 == 0
      def isOdd: Boolean = value % 2 == 1
      def sqrt: Double = Math.sqrt(value)
    }
    println(s"RichInt(42).sqrt: ${RichInt(42).sqrt}")
    println(s"42.sqrt: ${42.sqrt}")

    /*
      Exercises:  
      Enrich the String class
      1. asInt
      2. encrypt:  "John" -> Lnjp (character + 2)
      Enrich the Int class
      3. times(function)
          3.times(() => ...)
      4. * with List parameter
          3 * List(1,2) => List(1,2,1,2,1,2)
    */
    implicit class MyString(value: String):
      def asInt: Int = value.toInt  // course solution Integer.valueOf(value)
      def myencrypt: String = value.map(c => (c + 2).toChar) // course: value.map(c => (c + 2).asInstanceOf[Char])
    end MyString
    println(s"'40'.asInt + 2: ${"40".asInt + 2}")
    println(s"'John' encrypt: ${"John".myencrypt}")

    implicit class MyInt(value: Int):
      def times(f: () => Unit): Unit = (1 to value).foreach(_ => f())
      def *[T](list: List[T]): List[T] = (1 to value).toList.flatMap(_ => list)
    end MyInt
    println(s"3.times(() => println(3)): ${3.times(() => println(3))}")
    println(s"3 * List(1,2): ${3 * List(1,2)}")

    // implicit conversion
    implicit def stringToInt(string: String): Int = Integer.valueOf(string)
    println(s"'8' / 4: ${"8" / 4}")
 
  end part1
}
