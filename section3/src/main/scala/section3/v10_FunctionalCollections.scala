package section3

object v10_FunctionalCollections {
    def part1 =
      println("----- Part 1 -----")

      // Set
      //    1. sets behave like functions: set1(2)
      //    2. set is "callable" via apply() method
      val set1 = Set(1,2,3)
      println(s"set1(2), set1(42): ${set1(2)}, ${set1(42)}")
}


@main def v10_FunctionalCollectionsMain =
  println("---------- Section 3: Function Collections ----------")
  
  v10_FunctionalCollections.part1
  // v10_FunctionalCollections.exercise1
  
  println("\n---------- Section 3: Function Collections ----------") 