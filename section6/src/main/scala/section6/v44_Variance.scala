package section6

object v44_Variance:
  def part1() =
    println("part 1")

    trait Animal
    class Dog extends Animal
    class Puppy extends Dog
    class Cat extends  Animal
    class Kitty extends Cat
    class Crocodile extends Animal

    /*
      what is variance?
      "inheritance" - type substitution of generics

      For example: should a Cage[Cat] inherit from a Cage[Animal]?
        yes - covariance: Cage[+T] -> Cage[Cat] inherits from Cage[Animal], so this is okay: val cage: Cage[Animal] = Cage[Cate]
        no  - invariance -> this throws an compile time exception:  val cage: Cage[Animal] = Cage[Cat]
                            this also throws exception:             val cage: Cage[Cat] = Cage[Animal]
        no!!! - contravariance Cage[Animal] inherits from Cage[Cat], so this is okay: val cage: Cage[Cat] = Cage[Animal]
   
    
      General rule of thumb:
      - Use covariance when you have a COLLECTION OF THINGS
      - Use contravariance when you have a COLLECTION OF ACTIONS 
    */

    /*
      Invarient:  each class type variable produces a completely unrelated type, even if class types have an inheritence relationship
    */
    class Cage[T]     // invariant exception
    val cage1a: Cage[Cat] = Cage[Cat]()
    val cage1b: Cage[Animal] = Cage[Animal]()
    // val cage1c: Cage[Animal] = Cage[Cat]()    // throws exception: Cage[Animal] and Cage[Cat] have no relationship
    // val cage1d: Cage[Cat] = Cage[Animal]()    // throws exception: Cage[Animal] and Cage[Cat] have no relationship

    /*
      Covariant: Cage[T] is subtype of Cage[S] if T is SUBTYPE of S
        CageCovariant[Cat] is SUBTYPE of CageCovariant[Animal] because Cat is subtype of Animal
    */
    class CageCovariant[+T]   // covariant
    val cage2: CageCovariant[Animal] = CageCovariant[Cat]()

    /*
      Contravariant: Cage[T] is subtype of Cage[S] if T is a SUPERTYPE of S
        CageContravariant[Animal] is a SUBTYPE of CageContravariant[Cat] because Animal is a SUPERTYPE of Cat
    */    
    class CageContravariant[-T]   // contravariant
    val cage3: CageContravariant[Cat] = CageContravariant[Animal]()

  
    /*
        Variant Positions:
          1. Invariant does not worry about variant positions, each type derived by type parameter is independent
          2. Covariant
            1. Constructor parameter CAN accept a VAL type T
            2. Constructor parameter CAN NOT accept a VAR type T
            3. Method parameters are in CONTRAVARIANT position, so this is a problem
          3. Contravariant
            1. Constructor parameter CAN NOT accept a VAL type T - see example below
            2. Constructor parameter CAN NOT accept a VAR type T - same issue as 3.1
            3. Method parameter of type T are fine for contravariant type parameter
            4. Method return types are in COVARIANT postion, so this is a problem
    */

    //  invariant
    class CageInv[T](val animal: T)   // invariant

    /*
      2.1 example: covariant
        covariant position: class parameter accepts covariant type (sub type), but not contravariant (super type)
        this is because for a base T, a subtype can be used where a super type is defined, ie: val animal: Animal = new Cat()
    */
    class CageCov[+T](val animal: T)
    val cage4: CageCov[Animal] = new CageCov[Animal](new Crocodile())   // this is legal a Crocodile can be used where Animal defined
    

    /*
      3.1 And 3.2 example: contravariant
        - compiler will complain - contravariant type T in covariant position
        - problem: a plain type T can accept a subtype of T, so this would be valid if T was a class parameter:

          class Cage[-T](val animal: T) 
          val catCage: Cage[Cat] = new Cage[Animal](new Crocodile()) 
          // however, this is not valid because Crocodile can not go in a Cat class field - Crocodile is not a subtype of Cat
          
    */
    // class CageContra[-T](val animal: T)    // error: contravariant type T occurs in covariant position in type T of value animal
    // class CageContra[-T](var animal: T)    // error: contravariant type T occurs in covariant position in type T of value animal


    /*
      2.2 example: covariant
        - problem - a var can later be changed to something that is not valid

          class Cage[+T](var animal: T)
          val cage: Cage[Animal] = new Cage[Cat](new Cat)

          //  cage defined as Cage[Animal], but actually holds Cage[Cat]
          //    - Crocodile is an Animal, so assignment okay (val x: Animal = new Crocodile())
          //      however, cage is actually a Cage[Cat], so can't take a Crocodile
          cage.animal = new Crocodile
    */
    // class CageCov2[+T](var animal: T)   //  error: covariant type T occurs in contravariant position in type T of parameter animal


    /*
      2.3 example: covariant
        - problem 

          class Cage[+T]:
            def addAnimal(animal: T) = ???
          val cage: Cage[Animal] = Cage[Dog]  // okay because covariant

          //  should be able to add a Cat, since cage is defined as Cage[Animal] and Cat is a subtype of Animal
          //    however, is a problelm because cage is actually a Cage[Dog] (which is okay because Cage[Dog] is subtype of Cage[Animal])
          cage.addAnimal(new Cat)  // not okay, can't put Cat in Dog cage, since Cat is not subtype of Dog
    */
    // trait CageCov3[+T]:
    //   def addAnimal(animal: T): Unit  // error: covariant type T occurs in contravariant position in type T of parameter animal

    /*
      2.3 solution: widening the type for the method
    */
    class Cage2_3[+T]:
      // def add(animal: T): Cage2_3[T] = ???    // error: covariant type T occurs in contravariant position in type T of parameter animal
      def add[B >: T](animal: B): Cage2_3[B] = new Cage2_3[B]   // this "widens" the Cage type to accept any animal that is subtype of B
    val cage2_3: Cage2_3[Puppy] = Cage2_3[Puppy]()
    val cage2_3a = cage2_3.add(Dog())   // "widens" the type from Cage[Puppy] to Cage[Dog] to accomodate Dogs and Cats
    val cage2_3b = cage2_3a.add(Crocodile())  // "widens" the type from Cage[Dog] to Cage[Animal] to accomodate Crocodile


    /*
      3.3 example
    */
    class Cage3_3[-T]:
      def addAnimal(animal: T): Unit = println("added animal")
    val cage3_3: Cage3_3[Dog] = new Cage3_3[Animal]
    cage3_3.addAnimal(Dog())  // cage is defined as Dog, so can only accept a Dog or subtype of Dog
    cage3_3.addAnimal(Puppy())
    // cage3_3.addAnimal(Cat())  // error: can't put Cat in a Dog cage


    /*
      3.4 example: for contravariant definitions, method return types are in covariant position
        - problem

          class PetShop[-T]:
            def get(isItAPuppy: Boolean): T   // contravariant type T occurs in covariant position in type (isItAPuppy: Boolean): T of method get
          val catShop = new PetShop[Animal] {   // is valid, since this is defined as contravariant
            override def get(isItAPuppy: Boolean): Animal = Cat()  // if valid, since a Cat is a subtype of Animal
          }
          val dogShop: PetShop[Dog] = catShop   // is valid, since this is defined as contravariant and catShop is PetShop[Animal]
          val dog = dogShop.get(true)   // problem: should return Dog,  but the shop returns a Cat

        - solution: require method to return a subtype of contravariant type parameter (using '<:' contraint)

            class PetShop[-T]:
              def get[B <: T](isItAPuppy: Boolean, defaultAnimal: B): B = defaultAnimal
          
          
    */
    class PetShop[-T]:
      // def get(isItAPuppy: Boolean): T   // cerror: ontravariant type T occurs in covariant position in type (isItAPuppy: Boolean): T of method get
      def get[B <: T](isItAPuppy: Boolean, defaultAnimal: B): B = defaultAnimal

    val petShop3_4A: PetShop[Puppy] = new PetShop[Dog] {
      override def get[B <: Dog](isItAPuppy: Boolean, defaultAnimal: B): B = defaultAnimal
    }
    val dog3_4A = petShop3_4A.get(true, Puppy())   // okay, meets requirement B <: T
    // val cat = petShop.get(true, Cat())    // not okay, violates requirement B <: T

    val petShop3_4B: PetShop[Dog] = new PetShop[Animal] {
      override def get[B <: Animal](isItAPuppy: Boolean, defaultAnimal: B): B = defaultAnimal
    }
    val dog3_4B = petShop3_4B.get(true, Puppy())   // okay, meets requirement B <: T - Puppy is subtype of Dog
    val dog3_4C = petShop3_4B.get(true, Dog())   // okay, meets requirement B <: T - Dog is considered subtype of Dog
    // val cat = petShop3_4B.get(true, Cat())    // not okay, violates requirement B <: T (where T is Dog) - Cat is not subtype of Dog


    
  def main(): Unit =
    part1()

end v44_Variance
