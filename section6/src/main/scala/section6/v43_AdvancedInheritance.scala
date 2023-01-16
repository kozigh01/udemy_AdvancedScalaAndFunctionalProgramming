package section6

import java.io.Writer

object v43_AdvancedInheritance:

  trait Writer[T]:
    def write(value: T): Unit

  trait Closeable:
    def close(status: Int): Unit

  trait GenericStream[T] extends Writer[T], Closeable:
    def foreach(f: T => Unit): Unit

  def processStream[T](stream: GenericStream[T]) =
    stream.foreach(println)
    stream.close(0)


      
  
  def part1() =
    println("part1\n")

    // diamond problem
    trait Animal:
      def name: String
    trait Lion extends Animal:
      override def name: String = "Lion"
    trait Tiger extends Animal:
      override def name: String = "Tiger"

    class Mutant extends Lion, Tiger:
      override def name: String = "Liger"
    class Mutant2 extends Lion, Tiger
    class Mutant3 extends Tiger, Lion
    
    println(s"Mutant: ${Mutant().name}")

    /*
      Picks "Tiger":
        Mutant2
          extends Animal with { override def name: String = "Lion"}  -- from Lion
                        with { override def name: String = "Tiger"}  -- from Tiger

        Last one wins
    */
    println(s"Mutant2: ${Mutant2().name}")
    println(s"Mutant3: ${Mutant3().name}")
    

    // super problem + type linearization
    trait ColdColor:
      def print: Unit = println("cold")
    trait Green extends ColdColor:
      override def print: Unit = 
        println("green")
        super.print
    trait Blue extends ColdColor:
      override def print: Unit = 
        println("blue")
        super.print

    class Red:
      def print: Unit = println("red")
    
    class White extends Red, Green, Blue:
      override def print: Unit = 
        println("white")
        super.print


    /*
      Scala compiler goes through a process of "Type Linearization"
      1.  Cold = AnyRef with <Cold body>
          Green = Cold with <Green body> 
            => AyRef with <Cold body> with <Green body>
          Blue = Cold with <Blue body>
            => AnyRef with <Cold body> with <Blue body>
          Red = AnyRef with <Red body>

      2.  White = Red with Green with Blue with <White body>
            => AnyRef with <Red body>
                      with (AnyRef with <Cold body> with <Green body>)
                      with (AnyRef with <Cold body> with <Blue body>)
                      with <White body>

      3.  Type Linearization => reduce by skipping anything that has been seen before:
          White = AnyRef with <Red body> with <Cold body> with <Green body> with <Blue body> with <White body>
      
      4.  Follow the "Super()" chain starting with White:
            "white" => super.print() => "blue" => super.print() => "green" => super.print() => "coldmain"
    */
    White().print


  def main(): Unit = {
    part1()
  }
end v43_AdvancedInheritance

