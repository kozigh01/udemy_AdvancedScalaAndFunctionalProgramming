package section6

import scala.compiletime.ops.string

object v49_Recurive_FBounded {
  def part1() =
    // problem: want breed in trait to be type of sub-type 
    trait Animal:
      def breed: List[Animal]
    class Cat extends Animal:
      override def breed: List[Animal] = ???    // want a list of CAT
    class Dog extends Animal:
      override def breed: List[Animal] = ???  // want a list of DOg


    // solution 1 - niave:  due diligence in each sub-type, would prefer compiler help when mistakes are made
    trait Animal1:
      def breed: List[Animal1]
    class Cat1 extends Animal1:
      override def breed: List[Dog1] = ???    // this is okey, since List is covariant
    class Dog1 extends Animal1:
      override def breed: List[Cat1] = ???    // this is okey, since List is covariant


    // solution 2 - recursive type: F-Bounded Polymorphism
    trait Animal2[A <: Animal2[A]]:   // a recursive type: F-Bounded Polymorphism
      def breed: List[A]
    class Cat2 extends Animal2[Cat2]:
      override def breed: List[Cat2] = ???    // this is okey, since List is covariant
    class Dog2 extends Animal2[Dog2]:
      override def breed: List[Dog2] = ???    // this is okey, since List is covariant
    // still possible to make an error with F-Bounded Polymorphism
    class Crocodile2 extends Animal2[Dog2]:
      override def breed: List[Dog2] = ???

    // solution 3 - want compiler to enforce Animal[xxx] where xxx is the defining class
    //    F-Bounded Polymorphism + self-types
    trait Animal3[A <: Animal3[A]]: 
      self: A =>
      def breed: List[Animal3[A]]
    class Cat3 extends Animal3[Cat3]:
      override def breed: List[Cat3] = ???
    class Dog3 extends Animal3[Dog3]:
      override def breed: List[Dog3] = ???
    class Crocodile3 extends Animal3[Crocodile3]:
      override def breed: List[Crocodile3] = ???
    
    // however there is a problem with inheritance - at this point we give up on F-Bounded Polymorphism
    trait Fish3 extends Animal3[Fish3]
    class Shark extends Fish3:
      override def breed: List[Animal3[Fish3]] = ???
    class Cod extends Fish3:
      override def breed: List[Animal3[Fish3]] = List(new Shark)  // this compiles, but is wrong

    // //  solution 4: giving up on type classes - couldn't get it to work with Scala 3
    // trait Animal4
    // trait CanBreed[A]:
    //   def breed(a: A): List[A]

    // class Dog4 extends Animal4
    // object Dog4:
    //   given CanBreed[Dog4] = new CanBreed[Dog4] {
    //     override def breed(a: Dog4): String = s"I can breed: $a"
    //   }

    // // extension [A](cb: CanBreed[A])
    // //   def breed(using canBreed: CanBreed[A])(a: A): List[A] = cb.breed(a)

    // given canBreedConversion[A]: Conversion[CanBreed[A], Animal4] with
    //   def apply(cb: CanBreed[A]): Animal4 = new Animal4()
    //   // def apply(cb: CanBreed[A])(using cbi: CanBreed[A]): A = new Animal4()
    // given animalConversionToCanBreed[A]: Conversion[Animal4, CanBreed[A]] with
    //   def apply(cb: CanBreed[A]): Animal4 = new Animal4()
    //   // def apply(cb: CanBreed[A])(using cbi: CanBreed[A]): A = new Animal4()

    // val dog4 = Dog4
    // import Dog4.given_CanBreed_Dog4
    // println(s"Dog4.breed(dog4): ${dog4.breed}")  // can't get this solution to work with Scala 3

    // solution 5 - type classes alternate: giving up on type classes - couldn't get it to work with Scala 3
    trait Animal5[A]:
      def breed(a: A): List[A]

    class Dog5
    // class DogAnimal5 extends Animal5[Dog5]
    object Dog5:
      given dogToAnimal: Conversion[Dog5, Animal5[Dog5]] with
        def apply(dog: Dog5): Animal5[Dog5] = new Animal5 {
          def breed(a: Dog5): List[Dog5] = List(dog)
        }
      // given Animal5[Dog5] with
      //   def breed(a: Dog5)(using animal5: Animal5[Dog5]): List[Dog5] = animal5.breed(a)
      // given DogAnimal5 with {
      //   def breed(dog: Dog5): List[Dog5] = List()
      // }
    
      // extension [A](a: Animal5[A])
      //   def breed(): List[A]
    end Dog5
    
    import Dog5.*

    // given Conversion[Dog5, DogAnimal5] with
    //   def apply(animal: Dog5)(using animal5: DogAnimal5): DogAnimal5 = new DogAnimal5 {
    //     def breed(a: Dog5): List[Dog5] = animal5.breed(animal)
    //   }

    val dog5: Animal5[Dog5] = Dog5()
    // println(s"dog5.breed(): ${dog5.breed()}") 
      



    // may see this in ORM database libraries
    trait Entity[E <: Entity[E]]

    // also used with Comparable
    class Person extends Comparable[Person]:
      override def compareTo(x: Person): Int = ???


  @main def mainV49() =
    part1()
}
