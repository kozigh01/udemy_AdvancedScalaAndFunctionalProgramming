package section6

object v47_PathDependentType extends App:
  def part1() =
    println("Part 1")

    class Outer:
      class Inner
      object InnerObject
      type InnerType

      def print(i: Inner): Unit = println(i)
      def printGeneral(i: Outer#Inner): Unit = println(i)
    end Outer

    def method1 =
      class HelperClass
      type HelperType
    end method1


    // class members (including classes, traits, objects, types) are are available per-instance
    val outer1 = Outer()
    // val inner = Outer.Inner   // doesn't work for class Outer
    val inner = outer1.Inner()   // does work: o.Inner is a TYPE
    outer1.print(inner)

    val outer2 = Outer()
    val inner2 = outer1.Inner()
    // val inner3: outer2.Inner = outer.Inner()  // oerror: outer2.Inner different type than outer.Inner
    // outer2.print(inner2)  // error: outer.Inner not compatible with outer2.Inner
    outer2.print(outer2.Inner())


    // all Outer.Inner classes are subtypes of Outer#Inner
    println()
    outer1.printGeneral(outer1.Inner())    // Outer#Inner is super type to all outer.inner types
    outer1.printGeneral(outer2.Inner())
    outer2.printGeneral(outer1.Inner())    // Outer#Inner is super type to all outer.inner types
    outer2.printGeneral(outer2.Inner())


    /*
      Exercise
        DB keyed by Int or String, but maybe others

      use path-dependent types
      abstract type members andor type aliases
    */  
    trait ItemLike:
      type Key

    trait Item[K] extends ItemLike:
      type Key = K

    trait IntItem extends Item[Int]
    trait StringItem extends Item[String]
    // def get[T <: ItemLike](key: T#Key): T = ???


    // get[IntItem](42) // okay
    // get[StringItem]("scalarocks") // okay
    // get[IntItem]("nooooo!") // not okay



  end part1


  part1()

end v47_PathDependentType