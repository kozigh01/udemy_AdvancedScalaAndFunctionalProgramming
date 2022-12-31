package section4

import java.util.concurrent.Executors

object v21_IntoParallelProgramming {
  def part1() = 

    // JVM threads
    /*
      JVM Threads (java threads)

      interface Runnable {
        public void run()
      }
    */
    val thread1 = Thread(new Runnable {
      override def run(): Unit = println("Running in parallel")
    })
    thread1.start() // creates a JVM thread => OS thread: gives signal to JVM to start a JVM thread
    thread1.join()  // blocks until thread1 finishes running

    // differnt runs will produce different results - hello and goodbye in various orders
    val thread2 = Thread(() => (1 to 5).foreach(_ => println("hello")))
    val thread3 = Thread(() => (1 to 5).foreach(_ => println("goodbye")))
    thread2.start()
    thread3.start()

    // JVM theads are expensive -- want to use thread pools 
    val pool = Executors.newFixedThreadPool(10)

    pool.execute(() => println("something in the thread pool"))

    pool.execute(() => {
      Thread.sleep(1000)
      println("done after 1 second")
    })

    pool.execute(() => {
      Thread.sleep(1000)
      println("almost done")

      Thread.sleep(1000)
      println("done after 2 seconds")
    })

    pool.shutdown() // will throw exception for any subsequent pool.execute(...) calls, but finishes existing pool.execute(...) calls
    println(s"pool isShutdown: ${pool.isShutdown()}")
    // pool.execute(() => println("should not appear"))  // throws an exception in calling thread

    // pool.shutdownNow()  // interrupts any sleeping threads, which will throw exceptions


  def part2() = 
    println("do something else")
}
