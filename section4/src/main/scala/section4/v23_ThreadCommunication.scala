package section4

object v23_ThreadCommunication {
  /*
    Producer-Consumer problem

    producer -> [ x ] -> consumer  // need to force consumer to wait for producer
  */

  class SimpleContainer:
    private var value: Int = 0

    def isEmpty: Boolean = value == 0
    def set(newValue: Int) = 
      value = newValue
    def get() =
      val result = value
      value = 0 // reset the value to default
      result      
  end SimpleContainer

  def naiveProdCons(): Unit =
    val container = SimpleContainer()

    val consumer = Thread(() => {
      println("[consumer] waiting...")
      while(container.isEmpty) {  // busy waiting
        println("[consumer] actively waiting...")
        Thread.sleep(100)
      }

      println(s"[consumer] I have consumed ${container.get()}")
    })

    val producer = Thread(() => {
      println("[producer] computing...")
      Thread.sleep(500)  // simulate work
      val value = 42
      println(s"[producer] I have produced the value $value")
      container.set(value)
    })

    consumer.start()
    producer.start()
  end naiveProdCons

  def betterProdCons() =
    val container = SimpleContainer()

    val consumer = Thread(() => {
      println("[consumer] waiting...")
      container.synchronized {
        container.wait()        
        println(s"[consumer] I have consumed ${container.get()}")
      }
    })

    val producer = Thread(() => {
      println("[producer] computing...")
      Thread.sleep(2000)  // simulate work
      val value = 42
      container.synchronized {
        container.set(value)
        println(s"[producer] I have produced the value $value")
        container.notify()
      }
    })
    
    consumer.start()
    producer.start()
  end betterProdCons

  def part1() =
    println("part 1")  
    // naiveProdCons()
    betterProdCons()
}
