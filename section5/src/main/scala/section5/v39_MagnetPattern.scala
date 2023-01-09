package section5

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
    def receive(respnse: P2PResponse): Int
    def receive[T : Serializer](message: T): Int  // uses context bound T to eliminate implicit parameter
    def receive[T : Serializer](message: T, statusCode: Int): Int  // uses context bound T to eliminate implicit parameter
  
  def part1() =
    println("hello")


}
