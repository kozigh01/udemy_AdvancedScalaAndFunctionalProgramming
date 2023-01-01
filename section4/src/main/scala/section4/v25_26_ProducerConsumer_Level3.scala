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
  end part1_MySolution

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
  end part2_CourseSolution

}

object v26_ThreadCommunications:  
  /*
    1. think of an exaple where notifyAll acts in diferent way than notify
        - when there are consumers that are only reading and not using "synchronized"
    2. create a deadlock - all threads are blocking each other
    3. create a livelock - yield execution, but nobody is making progress: threads are not blocked
  */

  def exercise1_NotifyAll(): Unit =
    val bell = new Object

    (1 to 10).foreach(i => 
      Thread(() => {
        bell.synchronized {
          println(s"[thread $i] waiting...")
          bell.wait()
          println(s"[thread $i] bell is rung")
        }
      }
      ).start()
    )

    Thread(() => {
      Thread.sleep(2000)
      println(s"[announcer] let's ring the bell")
      bell.synchronized {
        // bell.notifyAll()
        bell.notify()
      }
    }).start()    
  end exercise1_NotifyAll

  def exercises2_Deadlock(): Unit =
    val list1 = List(1,2,3)
    println(s"exercise1 - start: list = $list1")
    
    val thread1 = Thread(() => {
      println(s"  exercise 1, thread1 - start: head = ${list1.head}")
      list1.synchronized {
        list1.wait()
      }
      println(s"  exercise 1, thread1 - end")
    })
    
    val thread2 = Thread(() => {
      println(s"  exercise 1, thread2 - start: tail = ${list1.tail}")
      list1.synchronized {
        while (true) {
          Thread.sleep(500)
          list1.wait()
        }
      }
      println(s"  exercise 1, thread2 - end")
    })
    
    val thread3 = Thread(() => {
      println(s"  exercise 1, thread3 - start: notify()")
      list1.synchronized {
        list1.notify()
      }
      println(s"  exercise 1, thread3 - end")
    })

    thread1.start()
    thread2.start()
    thread3.start()

    thread1.join()
    thread2.join()
    
    // Thread.sleep(2000)

    println(s"exercise1 - end: list = $list1")    
  end exercises2_Deadlock

  def exercises2_Deadlock_CourseSolution() =
    case class Friend(name: String):
      def bow(other: Friend): Unit =
        this.synchronized {
          println(s"$this: I am bowing to my friend $other")
          other.rise(this)
          println(s"$this:; my friend $other has risen")
        }
      end bow

      def rise(other: Friend): Unit =
        this.synchronized {
          println(s"$this: I am rising to my friend $other")
        }
      end rise
    end Friend 

    val sam = Friend("Sam")
    val bob = Friend("Bob")

    Thread(() => sam.bow(bob)).start()  // sam's lock, then bob's lock
    Thread(() => bob.bow(sam)).start()  // bob's lock, then sam's lock

    Thread.sleep(2000)
  end exercises2_Deadlock_CourseSolution

  def exercises3_Livelock_CourseSolution(): Unit =
    case class Friend(name: String):
      var side = "right"

      def switchSide(): Unit = {
        if side == "right" then "left"
        else "right"
      }
    end Friend
  end exercises3_Livelock_CourseSolution
end v26_ThreadCommunications
