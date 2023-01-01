package section4

import scala.collection.mutable
import scala.util.Random

object v24_ProducerConsumer_Level2 {
  def part1() =
    println("part1")

    /*
      Consider a buffer between producer(s) and consumer(s)
        producer -> [? ? ?] -> consumer  // a three position buffer
    */

    def prodConsLargeBuffer(): Unit =
      val buffer: mutable.Queue[Int] = mutable.Queue[Int]()
      val capacity = 3

      val consumer = Thread(() => {
        val random = Random()

        while(true) {
          buffer.synchronized {
            if buffer.isEmpty then {
              println("[consumer] buffer empty, waiting")
              buffer.wait()
            }

            // there must be at least one value in the buffer
            val x = buffer.dequeue()
            println(s"[consumer] consumed $x")

            buffer.notify()
          }

          Thread.sleep(random.nextInt(500))
        }
      })

      val producer = Thread(() => {
        val random = Random()
        var i = 0

        while(true) {
          buffer.synchronized {
            if buffer.size == capacity then {
              println("[producer] buffer is full, waiting")
              buffer.wait()
            }

            // there must be a least one empty space in the buffer
            println(s"[producer] producing $i")
            buffer.enqueue(i)

            buffer.notify()

            i += 1
          }

          Thread.sleep(random.nextInt(500))
        }
      })

      consumer.start()
      producer.start()
    end prodConsLargeBuffer

    prodConsLargeBuffer()
}
