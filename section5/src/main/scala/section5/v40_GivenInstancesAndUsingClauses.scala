package section5

object v40_GivenInstancesAndUsingClauses {
  def part1(): Unit =
    println("part 1")

    // old implicit way
//    object Implicits:
//      implicit val descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

    // new Scala 3 style
    //    given <=> implicit val (scala 2)
    // implicit val
    object GivenVals:
      given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

      // instantiating an anonymous class - Naive
      given descendingOrdering2: Ordering[Int] = new Ordering[Int] {
        override def compare(x: Int, y: Int) = y - x
      }
      // instantiating an anonymous class using with - better
      given descendingOrdering3: Ordering[Int] with {
        override def compare(x: Int, y: Int) = y - x
      }

    import GivenVals.{descendingOrdering3}

    println(List(4,6,2,3,7,1).sorted)

    val list1 = List(3,4,2,1)
    // import Implicits.{descendingOrdering}
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


  def main(args: Array[String]): Unit = {
    part1()
  }
}
