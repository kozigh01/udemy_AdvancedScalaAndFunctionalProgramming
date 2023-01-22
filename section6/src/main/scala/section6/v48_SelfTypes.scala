package section6

object v48_SelfTypes extends App {
  def part1() =
    /*
      Self Types

      * requiring a type to be mixed in
      * commonly used in "CAKE PATTERN"
      
      This is a different concept than inheritance
        class A
        class B extends A  // B is an A

        trait T
        trait S:    // this means S requires a T
          this: T =>  // self type
    */

    trait Instrumetalist:
      def play(): Unit

    trait Singer:
      self: Instrumetalist =>  // SELF TYPE -  implementation for Singer must also implement Instrumentalist

      def sing(): Unit

    class LeadSinger extends Singer, Instrumetalist:
      override def play(): Unit = ???
      override def sing(): Unit = ???


    val jamesHerfield = new Singer with Instrumetalist {  // will error at compile time if Instrumentalist not included
      override def play(): Unit = println("I'm playing")
      override def sing(): Unit = println("I'm singing, I'm singingggggg")
    }
    println(s"jamesHetfield: ${jamesHerfield.play()}, ${jamesHerfield.sing()}")


    class Guitarist extends Instrumetalist:
      override def play(): Unit = println("(guitar solo)")

    println(s"Guitarist: ${Guitarist().play()}")
  
  def part2() =

    // classical "dependency injection"
    class Component1:
      // API
      def doSomething(): Unit = ???

    class MyComponentA extends Component1:
      override def doSomething(): Unit = ???
    class MyComponentB extends Component1:
      override def doSomething(): Unit = ???

    class DependentComponent(val component: Component1):
      def useDependentComponent: Unit = ???


    // CAKE PATTERN => "dependency injection"
    trait Component2:
      // API
      def doSomething(): String = ???

    trait DependentComponentA:
      self: Component2 =>

      def dependentDoSomething(): Unit = doSomething() + " this rocks!"   // doSomething() is guaranteed to be available due to self-type

    
    // CAKE PATTERN example -- nodeling a web page layout
    trait UIComponent:
      def  action(x: Int): String
    trait UIDependentComponent:
      self: UIComponent =>
      def action2(): Unit
    // trait Application:   // can't figure out how to do this in Scala 3
    //   // self1: UIComponent =>
    //   self2: UIDependentComponent =>

    //   override def action(x: Int): String
    //   override def action2(): Unit


    // layer 1 - small components
    trait Picture extends UIComponent
    trait Stats extends  UIComponent

    // layer 2 - compose
    trait Profile extends UIDependentComponent with Picture
    trait Analytics extends UIDependentComponent with Stats

    // layer 3 - app
    // trait AnalyticsApp extends Application with Analytics


  def part3() =
    // cyclical dependencies
    // class X extends Y   // compile time error for cyclical dependency
    // class Y extends X   // compile time error for cyclical dependency


    // seems possible with self-type -- self-types are NOT ineritence, but are requirements instead
    trait X:
      self: Y =>
    trait Y:
      self: X =>

    

  
  @main def mainSelfType(): Unit =
    part2()
}
