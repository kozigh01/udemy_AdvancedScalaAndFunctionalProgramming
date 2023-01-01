package section4

import scala.collection.mutable
import scala.util.Random

object v25_ProducerConsumer_Level3 {
  def part1_MySolution() =
    println("part1 - My Solution")

    /*
      Multiple producers and multiple consumers
      Consider a buffer between producer(s) and consumer(s)
        producer1 -> [? ? ?] -> consumer1  // a three position buffer
        producer2 ....^   ^---- consumer2
    */

    def prodConsLargeBuffer(): Unit =
      val buffer: mutable.Queue[Int] = mutable.Queue[Int]()
      val capacity = 3

      def createConsumer(sleepDurationMax: Int, name: String): Thread = 
        Thread(() => {
          val random = Random()

          while(true) {
            buffer.synchronized {
              while (buffer.isEmpty) {
                println(s"[$name] buffer empty, waiting")
                buffer.wait()
              }

              // there must be at least one value in the buffer
              val x = buffer.dequeue()
              println(s"[$name] consumed $x")

              buffer.notify()
            }

            Thread.sleep(random.nextInt(sleepDurationMax))
          }
        })

      val consumer1 = createConsumer(500, "consumer 1")
      val consumer2 = createConsumer(250, "consumer 2")

      def createProducer(sleepDurationMax: Int, name: String) = 
        Thread(() => {
          val random = Random()
          var i = 0

          while(true) {
            buffer.synchronized {
              while (buffer.size == capacity) {
                println(s"[$name] buffer is full, waiting")
                buffer.wait()
              }

              // there must be a least one empty space in the buffer
              println(s"[$name] producing $i")
              buffer.enqueue(i)

              buffer.notify()

              i += 1
            }

            Thread.sleep(random.nextInt(500))
          }
        })
      val producer1 = createProducer(500, "producer 1")
      val producer2 = createProducer(250, "producer 2")

      consumer1.start()
      consumer2.start()
      producer1.start()
      producer2.start()

    end prodConsLargeBuffer

    prodConsLargeBuffer()

  def part2_CourseSolution() =
    println("part2 - Course Solution")

    class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread:
      override def run(): Unit = 
        val random = Random()
        val name = s"consumer $id"

        while(true) {
          buffer.synchronized {
            while (buffer.isEmpty) {
              println(s"[$name] buffer empty, waiting")
              buffer.wait()
            }

            // there must be at least one value in the buffer
            val x = buffer.dequeue()
            println(s"[$name] consumed $x")

            buffer.notify()
          }

          Thread.sleep(random.nextInt(500))
        }
      end run
    end Consumer
    
    class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread:
      override def run(): Unit = 
        val random = Random()
        val name = s"consumer $id"
        var i = 0

        while(true) {
          buffer.synchronized {
            while (buffer.size == capacity) {
              println(s"[$name] buffer is full, waiting")
              buffer.wait()
            }

            // there must be a least one empty space in the buffer
            println(s"[$name] producing $i")
            buffer.enqueue(i)

            buffer.notify()

            i += 1
          }

          Thread.sleep(random.nextInt(250))
        }
      end run
    end Producer

    def multiProducersConsumers(nConsuers: Int, nProducers: Int): Unit =
      val buffer: mutable.Queue[Int] = mutable.Queue[Int]()
      val capacity = 3

      (1 to nConsuers).foreach(i => {
        Consumer(i, buffer).start()
      })

      (1 to nProducers).foreach(i => {
        Producer(i, buffer, capacity).start()
      })
    end multiProducersConsumers

    multiProducersConsumers(3,3)
}
