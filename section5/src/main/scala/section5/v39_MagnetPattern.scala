package section5

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object v39_MagnetPattern {
  /*
    Magnet Pattern:
      - Use case of type classes that aims to solve some problems with 
        method overloading
  */

  class P2PRequest
  class P2PResponse
  class Serializer[T]

  trait Actor:
    def receive(statusCode: Int): Int
    def receive(request: P2PRequest): Int
    def receive(response: P2PResponse): Int
    def receive[T : Serializer](message: T): Int  // uses context bound T to eliminate implicit parameter
    def receive[T : Serializer](message: T, statusCode: Int): Int  // uses context bound T to eliminate implicit parameter
    def receive(future: Future[P2PRequest]): Int
    // def receive(future: Future[P2PResponse]): Int // type erasure: compile error because generics are erased at compile time, so clashes with line above
    // lots of overloads

    /*  Problems with many overloads:
      1. type erasure - see example above for Future
      2. lifting doesn't work for all overloads
          val receiveFV = receive _  // what does _ mean: statusCode, request, response, ???
      3. code duplication is probable
      4. type inference and default args
          actor.receive(?!)  - actor receive get what?
    */

  // Alternate - Magnet pattern
  sealed trait MessageMagnet[Result]:
    def apply(): Result

//  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int]:
//    override def apply(): Int = {
//      println("handling P2P request")
//      42
//    }
  implicit class FromP2PResponse(response: P2PResponse) extends MessageMagnet[Int]:
    override def apply(): Int = {
      println("handling P2P response")
      43
    }
  given Conversion[P2PRequest, MessageMagnet[Int]] with
    def apply(x: P2PRequest): MessageMagnet[Int] =
      println("handling P2PRequest with given Conversion")
      new MessageMagnet[Int] {
        override def apply(): Int = 44
      }

  def receive[R](magnet: MessageMagnet[R]): R = magnet()   // calls the magnet apply method

  //  1. no more type erasure problems!
  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = 2
  }
  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply(): Int = 2
  }

  // 2. lifting works
  trait MathLib:
    def add1(x: Int): Int = x + 1
    def add1(s: String): Int = s.toInt + 1

  trait AddMagnet:
    def apply(): Int

  def add1(magnet: AddMagnet): Int = magnet()

  implicit class AddInt(x: Int) extends AddMagnet:
    override def apply(): Int = x + 1
//  implicit class AddString(s: String) extends AddMagnet:
//    override def apply(): Int = s.toInt + 1
  given Conversion[String, AddMagnet] with
    override def apply(s: String): AddMagnet = s.toInt
  val addFV = add1 _


  /*  Magnet Pattern Drawbacks
    1. verbose
    2. harder to read
    3. you can't name or place default arguments
    4. call by name doesn't work
  */


  def part1(): Unit =
    println("hello")

    println(receive(P2PRequest()))
    println(receive(P2PResponse()))

    println(receive(Future(new P2PRequest)))
    println(receive(Future(new P2PResponse)))

    println(add1(6))
    println(addFV("7"))

  @main def main()=
    part1()

}
