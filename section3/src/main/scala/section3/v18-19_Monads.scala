package section3

object v18_Monads {

  // our own version of Try monad
  trait Attempt[+A]:
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  object Attempt:
    def apply[A](a: => A): Attempt[A] = 
      try
        Success(a)
      catch
        case e: Throwable => Failure(e)


  case class Success[A](value: A) extends Attempt[A]:
    def flatMap[B](f: A => Attempt[B]): Attempt[B] = 
      try
        f(value)
      catch
        case e: Throwable => Failure(e)

    
  case class Failure(e: Throwable) extends Attempt[Nothing]:
    def flatMap[B](f: Nothing => Attempt[B]): Attempt[Nothing] = this


  def part1 = 
    println("----- Part 1 -----")

    /*
      Prove monad laws for Attempt
      1. left-identity: unit.flatMap(f) = f(x)
        Attempt(x).flatMap(f) = f(x)  // success case
        Success(x).flatMap(f) = f(x)  // proved
      2. right-identity: attempt.flatMap(unit) = attempt
        Success(x).flatMap(x => Attempt(x)) = Attept(x) = Success(x)
        Failure(_).flatMap(...) = Fail(e)
      3. associativity: attempt.flatMap(f).flatMap(g) = attempt.flatMap(x => f(x).flatMap(g))
        Failure(e).flatMap(f).flatMap(g) = Failure(e) === Failure(e).flatMap(x => f(x).flatMap(g)) = Failure(e) 

        Success(v).flatMap(f).flatMap(g) = f(v).flatMap(g) OR Failure(e)
        Success(w).flatMap(x => f(x).flatMap(g)) = f(w).flatMap(g) OR Failure(e)  // same result as line above
    */

    val attempt = Attempt {
      throw new RuntimeException("My own monad, yes!")
    }
    println(s"attempt: $attempt")
  

  // trait LazyT[A]:
  //   def flatMap[B](f: A => Lazy[B]): Lazy[B]
  // object LazyT:
  //   def apply[A](value: => A): LazyT[A] = Lazy(value)
  // class Lazy[A](value: => A) extends LazyT[A]:
  //   lazy val lazyValue = value
  //   def flatMap[B](f: A => Lazy[B]): Lazy[B] = Lazy(f(value).value)
  class Lazy[+A](value: => A):
    // evalute value only once
    private lazy val internalValue = value  // "call by need" -- try commenting out and seeing the differenc

    def get(): A = internalValue
    def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(internalValue)  // this uses the "call by name" value constructor arg

    // exercise 2
    def map[B](f: A => B): Lazy[B] = flatMap(x => Lazy(f(x)))
    // def flatten[B >: A](m: Lazy[Lazy[B]]): Lazy[B] = m.flatMap(x => x)
  object Lazy:
    def apply[A](value: => A): Lazy[A] = {
      println(s"   --- new Lazy: start ---")
      val newLazy = new Lazy(value) // this satisfies Monad "unit" requirement
      println("   --- new Lazy: end ---")
      newLazy
    } 
    
    // exercise 2
    def flatten[A](m: Lazy[Lazy[A]]): Lazy[A] = m.flatMap(x => x)

  def exercise1 =
    println("----- Excersice 1: start -----")

    val lazy1 = Lazy {
      println("       lazy1 is being evaluated")
      42
    }

    val lazy2 = lazy1.flatMap {
      println("       lazy2 is being crated in flatMap")
      x => Lazy(x + 3)
    }

    // exercise 2
    val lazy3map = lazy1.map(_ * 10)
    val lazy3flatten = Lazy.flatten(Lazy(Lazy(22)))

    println("   --- using lazy1 ---")
    println(s"       lazy1.get(): ${lazy1.get()}")  // use of lazy1 to trigger evaluation
    println("   --- used lazy1 ---")

    println("   --- using lazy2 ---")
    println(s"       lazy2.get(): ${lazy2.get()}")  // use of lazy1 to trigger evaluation
    println("   --- used lazy2 ---")

    println("   --- using lazy3map ---")
    println(s"       lazy3map.get(): ${lazy3map.get()}")  // use of lazy1 to trigger evaluation
    println("   --- used lazy3map ---")

    println("   --- using lazy3flatten ---")
    println(s"       lazy3flatten.get(): ${lazy3flatten.get()}")  // use of lazy1 to trigger evaluation
    println("   --- used lazy3flatten ---")
  
    println("----- Excersice 1 end -----")
  }


@main def v18_MonadsMain =
  println("---------- Section 3: Monads ----------")
  
  // v18_Monads.part1
  v18_Monads.exercise1
  
  println("\n---------- Section 3: Monads ----------")  