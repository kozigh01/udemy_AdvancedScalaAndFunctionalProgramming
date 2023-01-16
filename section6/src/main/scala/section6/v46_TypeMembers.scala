package section6

object v46_TypeMembers extends App {

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection:
    // abstract type members
    type AnimalType
    type BoundedAnimal <: Animal
    type SuperBoundedAnimal >: Dog <: Animal

    // type alias
    type AnimalC = Cat


  def part1() =
    println("part 1")

    val ac = new AnimalCollection
    val dog: ac.AnimalType = ???  // not useful, what do we instantiate here?

    // val cat: ac.BoundedAnimal = Cat()  // error: compiler doesn't know what BoundedAnimal is, could be Crocodile

    val pup: ac.SuperBoundedAnimal = Dog()
    val cat: ac.AnimalC = Cat()

    type CatAlias = Cat
    val cat2: CatAlias = Cat()


    // Alternative to generics
    trait MyList:
      type T
      def add(element: T): MyList

    class NonEmptyList(value: Int) extends MyList:
      override type T = Int
      override def add(element: T): MyList = ???


    // .type
    type CatsType = cat.type
    val newCat: CatsType = cat
    // val doesntWork = new CatsType  // compiler doesn't know what to do here
  
  
  
  part1()
}
