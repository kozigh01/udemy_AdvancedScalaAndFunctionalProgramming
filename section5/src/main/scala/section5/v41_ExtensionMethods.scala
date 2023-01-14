package section5

object v41_ExtensionMethods:
  def part1() =
    println("part 1")

    case class Person(name: String):
      def greet(): String = s"Hi, I'm $name, how can I help?"

    // old scala 2 way using implicit class
    implicit class PersonLike(name: String) {
      def greet(): String = Person(name).greet()
    }
    println("Mark".greet())

    // new scala 3 way using extension
    extension (string: String) {
      // define all additional methods
      def greetAsPerson(): String = Person(string).greet()
    }
    println("Dave".greetAsPerson())

    // generic extensions
    extension [A](list: List[A]) {
      def ends: (A, A) = (list.head, list.last)
      def extremes(using ordering: Ordering[A]): (A, A) = list.sorted.ends
    }
    println(List(6,2,5,4,9,3).ends)
    println(List(6,2,5,4,3,9).extremes)


  def main(args: Array[String]): Unit =
    part1()