package section5

object v33_OrganizingImplicits {
  def part1() =

    // implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
    implicit def reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)  // alt using def: must NOT use parentheses
    println(List(3,7,2,6,8,4).sorted)   // sorted has implicit Ordering parameter

    /*
      Implicits (used as implicit parameters):
        - val/var
        - object
        - accessor methods = defs with no parentheses
    */

    // Exercise
    case class Person(name: String, age: Int)

    // this will work -- it's  a valid compainion object
    // object Person:
      // implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((p1, p2) => p1.name.compareTo(p2.name) < 0)
    // this will not work -- compiler will not look inside this object (it's not a valid compainion)
    // object SomeObject:
    //   implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((p1, p2) => p1.name.compareTo(p2.name) < 0)

    // these implicits will take precedence over those defined in the companion class - local scope has higher precedence
    // implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((p1, p2) => p1.name.compareTo(p2.name) < 0)
    // implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((p1, p2) => p1.age < p2.age)


    // can group implicits in an object, then import the one needed in scope where needed
    object AlphabeticNameOrdering:
      implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((p1, p2) => p1.name.compareTo(p2.name) < 0)

    object AgeOrdering:
      implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((p1, p2) => p1.age < p2.age)
    

    val persons = List(
      Person("Steve", 30),
      Person("Amy", 22),
      Person("John", 66)
    )
    
    // will use whichever imported implicit is imported last
    import AlphabeticNameOrdering.personOrdering
    import AgeOrdering.personOrdering

    println(s"persons.sorted: ${persons.sorted}")

    /*
      Implicit scope precidence
        - normal scope = LOCAL SCOPE
        - mporte scope 
        - companion objects of all types involved in the method signature
          - ex: def sorted[B >: A](implicit ord: Ordering[B]): List
            Will looK:
            - List
            - Ordering
            - all the types involved = A or and supertype
    */


    /*
      Exercises:
        3 orderings
          - total price: most used (50%)
          - by unit count: some used (25%)
          - by unit price: some used (25%)
    */
    case class Purchase(nUnits: Int, unitPrice: Double):
      def totalPrice = nUnits * unitPrice

    object Purchase:
      implicit val orderByTotalPrice: Ordering[Purchase] = Ordering.fromLessThan((a, b) => a.totalPrice < b.totalPrice)

    object PurchaseSortings:
      implicit val orderByUnitCount: Ordering[Purchase] = Ordering.fromLessThan((a,b) => a.nUnits < b.nUnits)
      implicit val orderByUnitPrice: Ordering[Purchase] = Ordering.fromLessThan((a,b) => a.unitPrice < b.unitPrice)

    val purchases = List(
      Purchase(5, 1.5),
      Purchase(12, 0.50),
      Purchase(2, 5.0),
      Purchase(7, 2.75)
    )

    // import PurchaseSortings.orderByUnitCount
    // import PurchaseSortings.orderByUnitPrice
    println(s"purchases.sorted: ${purchases.sorted}")
}
