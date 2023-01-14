package section5

object v40_GivenInstancesAndUsingClauses {
  def part1(): Unit =
    println("part 1")

    // old implicit way
    object Implicits:
      implicit val descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

    // new Scala 3 style
    //    given <=> implicit val (scala 2)
    // implicit val
    object GivenValues:
      // given value
      given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
    object GivenAnonymous:
      // instantiating an anonymous class - Naive
      given descendingOrdering2: Ordering[Int] = new Ordering[Int] {
        override def compare(x: Int, y: Int) = y - x
      }
    object GivenWith:
      // instantiating an anonymous class using "with" - better
      given descendingOrdering3: Ordering[Int] with {
        override def compare(x: Int, y: Int) = y - x
      }

//    import GivenWith.{descendingOrdering3}
//    import GivenWith.given
    import GivenWith.*  // doesn't import givens: need to use import GivenWith.given

    //    import Implicits.{descendingOrdering}

    println(List(4,6,2,3,7,1).sorted)

    val list1 = List(3,4,2,1)
    println(list1.sorted)


    // Implicit arguments <=> using clauses
    def extremes[A](list: List[A])(implicit ordering: Ordering[A]): (A,A) =
      val sortedList = list.sorted
      (sortedList.head, sortedList.last)

    def extremes2[A](list: List[A])(using ordering: Ordering[A]): (A,A) =
      val sortedList = list.sorted
      (sortedList.head, sortedList.last)

    println(extremes(List(4, 6, 2, 3, 7, 1)))
    println(extremes2(List(4, 6, 2, 3, 7, 1)))


    // implicit defs (synthesize new implicit values)
    trait Combinator[A]:  // in mathematical terms, this is a semigroup
      def combine(x: A, y: A): A

    // compare lists of ints based on sum of elements: List(1,2,3) < List(4,5,6) (6 < 15)

    // scala 2 method
    implicit def ListOrdering[A](implicit simpleOrdering: Ordering[A], combinator: Combinator[A]): Ordering[List[A]] =
      new Ordering[List[A]]:
        override def compare(x: List[A], y: List[A]): Int =
          val sumX = x.reduce(combinator.combine)
          val sumY = x.reduce(combinator.combine)
          simpleOrdering.compare(sumX, sumY)
    //  scala 3 method
    given listOrdering2[A](using simpleOrdering: Ordering[A], combinator: Combinator[A]): Ordering[List[A]] with {
      override def compare(x: List[A], y: List[A]): Int =
        val sumX = x.reduce(combinator.combine)
        val sumY = x.reduce(combinator.combine)
        simpleOrdering.compare(sumX, sumY)
    }


    // implicit conversions (abused in scala 2)
    case class Person(name: String) {
      def greet(): String = s"Hi, my name is $name"
    }

    // scala 2 implicit conversion
//    implicit def string2Person(name: String): Person = Person(name)
//    val danielGreet = "Daniel".greet()   // compiler: string2Person("Daniel").greet()
//    println(danielGreet)

    // scala 3 conversion
    import scala.language.implicitConversions   // required for scala 3
    given string2PersonConversion: Conversion[String, Person] with {
      override def apply(name: String): Person = Person(name)
    }
    println("Mark".greet())


  def main(args: Array[String]): Unit = {
    part1()
  }
}
