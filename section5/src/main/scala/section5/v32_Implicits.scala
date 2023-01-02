package section5

object v32_ImplicitsIntro:
  def part1() =
    println("part 1")
    val pair = "Daniel" -> "555"
    val intPair = 1 -> 2

    case class Person(name: String):
      def greet = s"Hi, my name is $name"
    
    case class Animal(name: String):
      def greet = s"grrrrr, I'm $name"

    implicit def fromStringToPerson(str: String): Person = Person(str)
    // implicit def fromstringToAnimal(str: String): Animal = Animal(str) // causes issue with ambiguity

    println("Peter".greet)  // implicit conversion: fromStringToPerson("Peter").greet

    // implicit parameters
    def increment(x: Int)(implicit amount: Int): Int = x + amount
    implicit val defaultAmount: Int = 11
    println(s"increment(4): ${increment(4)}")   // implicitely uses defaultAmount
    println(s"increment(4)(3): ${increment(4)(3)}")   // implicitely uses defaultAmount
    
end v32_ImplicitsIntro