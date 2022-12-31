package section4

import scala.annotation.tailrec

object v22_ConcurrencyProblems:
  
  def part1() =
    def runInParallel(): Unit =
      var x = 0

      var thread1 = Thread(() => {
        x = 1
      })
      var thread2 = Thread(() => {
        x = 2
      })

      thread1.start()
      thread2.start()

      Thread.sleep(1000)
      println(s"x = $x")  // results in a race condition between threads because of mutable var's
    
    end runInParallel
    
    runInParallel()

  def part2() =
    case class BankAccount(var amount: Int)

    def buySafe(account: BankAccount, thing: String, price: Int): Unit =
      account.synchronized {  // synchronized establishes a 'critical section' which only allows one thread at a time
        account.amount -= price // the 'critical'
      }

    def buy(account: BankAccount, thing: String, price: Int): Unit =
      account.amount -= price
    
    def demoBankingProblem(): Unit =
      println("running demo bank problem")
      (1 to 10000).foreach { _ =>
        val account = BankAccount(50000)
        
        val thread1 = Thread(() => buySafe(account, "shoes", 3000))
        val thread2 = Thread(() => buySafe(account, "iPhone", 4000))
        
        // val thread1 = Thread(() => buy(account, "shoes", 3000))
        // val thread2 = Thread(() => buy(account, "iPhone", 4000))

        thread1.start()
        thread2.start()

        thread1.join()
        thread2.join()

        if account.amount != 43000 then 
          println(s"AHA! I've just broken the bank: ${account.amount}")
        end if
      }
    end demoBankingProblem

    demoBankingProblem()
  
  end part2

  def exercises() =
    @tailrec
    def inceptionThreads(n: Int): Unit =
      val thread1 = Thread(() => {
        println(s"hello from thread $n")        
      })
      thread1.start()
      thread1.join()  // blocks waiting for thread1 to finish
      if n > 0 then inceptionThreads(n - 1)
    end inceptionThreads

    // course solution
    def inceptionThreadsCourse(maxThreads: Int, i: Int = 1): Thread =
      Thread(() => {
        if i < maxThreads then
          val childThread = inceptionThreadsCourse(maxThreads, i + 1)
          childThread.start()
          childThread.join()
        end if
        println("hello from thread $i")
      })

    inceptionThreads(10)


    /*
      exercise 2: what are the max and min values for demoSleepFallacy
      Answers:
        1. max value is 100 (increment for each thread)
        2. min value is 1 - if race condition on all threads and first is only one to write
          - all threads read x = 0
          - all threads increment 0
          - all threads write, but all have 1
    */
    def minMaxX(): Unit =
      var x = 0
      val threads = (1 to 100).map(_ => Thread(() => x += 1))
      threads.foreach(_.start())
    end minMaxX


    /*
      almost always, message = "Scala is awesome"
      is it guaranteed? NO

      Obmoxious situation (possible):
        main thread:
          message = "Scala sucks"
          awesomeThread.start()
          sleep(1001) - can yield execution on some systems
        awesome thread:
          sleep(1000) - can yield execution on some systems
        OS gives the CPU to some unrelated thread that takes > 2s
        OS give the CPU back to the main thread
        main thread:
          println(message)  // here message is "Scala sucks"
        awesome thread:
          message = "Scala is awesome"
          this comes to late, as the main thread has already printed the message

        solution:  join the worker thread before calling the println(message) in main thread
    */
    def demoSleepFallacy(): Unit =
      var message = ""
      val awesomeThread = Thread(() => {
        Thread.sleep(1000)
        message = "Scaaa is awesome"
      })

      message = "Scala sucks"
      awesomeThread.start()
      Thread.sleep(1001)

      // solution: join worker thread
      // awesomeThread.join()

      println("demoSleepFallacy message: $message")
    end demoSleepFallacy

  end exercises


    


end v22_ConcurrencyProblems
