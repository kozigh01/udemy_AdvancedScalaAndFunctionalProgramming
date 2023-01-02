package section4

import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Random}
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Promise
import scala.util.Try

object v27_FuturesAndPromises:
  def part1(): Unit =
    def longCalculation: Int = 
      Thread.sleep(2000)
      42

    val future1 = Future {
      // throw new RuntimeException("long calc failed")
      longCalculation   // calculates method on a different thread
    }
    println(future1.value)  // returns Option[Try[Int]] - option because may not be done yet

    println("waiting on the future...")
    future1.onComplete {  // onComplete returns Unit, so is used for side-effects and epression value not used
        // t => t match   // this is a partial function, so can leave this line out
          case Success(value) => println(s"the long calculation returned: $value")
          case Failure(exception) => println(s"the long calculation failed with exception: $exception")
    }   // future is processed by an unspecified thread -- could be main, could be other thread

    Thread.sleep(3000)
  end part1
end v27_FuturesAndPromises

object v28_FuturesAndPromises2:
  def part1(): Unit =
    case class Profile(id: String, name: String):
      def poke(otherProfile: Profile) =
        println(s"${this.name} poking ${otherProfile.name}")
    end Profile

    object SocialNetwork:
      // "database" of profiles
      val names = Map(
        "fb.id.1-zuck" -> "Mark",
        "fb.id.2-bill" -> "Bill",
        "fb.id.0-dummy" -> "Dummy"
      )
      val friends = Map(
        "fb.id.1-zuck" -> "fb.id.2-bill"
      )

      val random = Random()

      // API
      def fetchProfile(id: String): Future[Profile] =
        Future {
          Thread.sleep(random.nextInt(300)) // simulates time needed to retrieve from database
          // throw new RuntimeException("profile not found")
          Profile(id, names(id))
        }
      end fetchProfile

      def fetchBestFriend(profile: Profile): Future[Profile] =
        Future {
          Thread.sleep(random.nextInt(400))
          val bfId = friends(profile.id)
          Profile(bfId, names(bfId))
        }
      end fetchBestFriend
    end SocialNetwork

    // client: mark to poke bill
    val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
    mark.onComplete {
      case Success(profile) => {
        val bff = SocialNetwork.fetchBestFriend(profile)
        bff.onComplete {
          case Success(bff) => profile.poke(bff)
          case Failure(exception) => println(s"can't fetch best friend: $exception")
        }
      } 
      case Failure(exception) => println(s"can't retrieve profile: $exception")
    } // this works, but is kinda ugly

    Thread.sleep(1000)

    //  functional composition of futures
    //    map, flatMap, filter -- will propogate excption from calling future
    val nameOnTheWall = mark.map(profile => profile.name) // profile -> string: will get exception from original future if exists
    val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
    val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

    // since we have map, flatMap and filter - can use for-comprehensions
    //    MUCH NICER than the onComplete structure from above
    val result = for
      mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
      bill <- SocialNetwork.fetchBestFriend(mark)
    yield {
      mark.poke(bill)
    }

    for
      err <- result.failed
    yield println(s"the result failed: $err")

    result.onComplete {
      case Success(value) => println(s"success: $value")
      case Failure(exception) => println(s"failure: $exception")
    }

    Thread.sleep(1000)

    // fallbacks
    SocialNetwork.fetchProfile("bad id").onComplete {
      case Success(profile) => println(s"bad id result: $profile")
      case Failure(ex) => println(s"bad id exception: $ex")
    }   

    val aProfileNoMatterWhat = SocialNetwork.fetchProfile("bad id 2").recover {
      case e: Throwable => Profile("fb.id.0-dummy", "Forever alone")
    }
    for
      profile <- aProfileNoMatterWhat
    yield println(s"aProfileNoMatterWhat: $profile")

    val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("bad id 3").recoverWith {
      case _: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
    }
    for
      profile <- aFetchedProfileNoMatterWhat
    yield println(s"aFetchedProfileNoMatterWhat: $profile")

    val fallbackResult = SocialNetwork.fetchProfile("bad id 4").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))
    fallbackResult.onComplete {
      case Success(value) => println(s"fallbackResult success: $value")
      case Failure(exception) => println(s"fallbackesult failure: $exception")
    }
    fallbackResult.failed.onComplete {
      case Success(value) => println(s"fallbackResult.failed success: $value")
      case Failure(exception) => println(s"fallbackesult.failed failure: $exception")
    }

    Thread.sleep(2000)  

  end part1
end v28_FuturesAndPromises2

object v29_FuturesAndPromises3:
  // online backing app
  case class User(name: String)
  case class Transaction(sender: String, reciever: String, amount: Double, status: String)

  object BankingApp:
    val name = "Rock the JVM banking"

    def fetchUser(name: String): Future[User] =
      Future {
        Thread.sleep(500)   // simulate retrieving the user work
        User(name)
      }
    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] =
      Future {
        // simulate some processes
        Thread.sleep(1000)
        Transaction(user.name, merchantName, amount, "SUCCESS")
      }
    def purchase(username: String, item: String, merchantName: String, cost: Double): String =
      // fetch the user from the db
      // create a transaction
      // WAIT for the transaction to finish

      val transactionStatusFuture =
        for 
          user <- fetchUser(username)
          transaction <- createTransaction(user, merchantName, cost)
        yield transaction.status

      // will block until all futures are completed
      Await.result(transactionStatusFuture, 2.seconds) // implicit conversion

    end purchase  
  end BankingApp

  def part1() =
    // block on a future
    val purchase = BankingApp.purchase("Daniel", "Galaxy S22", "the store", 800)
    println(purchase)
  end part1

  def part2() =
    // promises
    val promise1 = Promise[Int]()   // "controller" over a future
    val future1 = promise1.future

    // thread 1 - "consumer"
    future1.onComplete {
      case Success(value) => println(s"[consumer] I've received $value")
      case Failure(exception) => println(s"[consumer] There was an error: $exception")
    }

    // thread 2 - "producer"
    val producer = Thread(() => {
      println("[producer] crunching numbers...")
      Thread.sleep(1000)

      // fullfilling the promise
      promise1.success(42)
      // promise1.failure(new RuntimeException("I fail"))
      println("[producer] done")
    }).start()

    Thread.sleep(3000)
  end part2
end v29_FuturesAndPromises3

object v30_FuturesAndPromisesExercises:
  def createFuture[T](exerciseNumber: Int, futureName: String, sleepDuration: Int, value: T): Future[T] =
    Future {
      println(s"#$exerciseNumber: starting $futureName")
      Thread.sleep(sleepDuration)
      println(s"#$exerciseNumber: finishing $futureName")
      value
    }
  end createFuture

  def exercises(): Unit =
    //  1. fulfill a future immediately with a value
    val future1 = Future(42)
    future1.onComplete {
      case Success(value) => println(s"#1: successfully returned $value")
      case Failure(exception) => println(s"#1: I failed: $exception")
    }
    Thread.sleep(200)

    // 2. inSequence(futurea, futureb) - run future b after future a completed
    println("\nexercise 2:")
    def inSequence[T](fa: Future[T], fb: Future[T]): Future[T] =
      fa.flatMap(_ => fb)

    def inSequence2[T](fa: Future[T], fb: Future[T]): Future[T] =
      for
        result1 <- fa
        result2 <- fb
      yield result2

    val fa2 = createFuture(2, "fa", 300, 42)
    val fb2 = createFuture(2, "fb", 300, 43)

    val result2_a = inSequence(fa2, fb2)
    Thread.sleep(1000)
    println(s"#2 result: $result2_a")

    val result2_b = inSequence2(fa2, fb2)
    Thread.sleep(1000)
    println(s"#2 result2: $result2_b")

    // 3. first(futurea, futureb) => returns first value of 2 futures to return
    println("\nexercise 3:")
    def first[T](fa: Future[T], fb: Future[T]): Future[T] =
      while !fa.isCompleted && !fb.isCompleted do
        Thread.sleep(10)
      
      if fa.isCompleted then fa.map(x => x)
      else fb.map(x => x)
    end first

    val result3_a = first(
      createFuture(3, "fa", 200, 42),
      createFuture(3, "fb", 300, 43)
    )
    Thread.sleep(1000)
    println(s"#3 result a: $result3_a")

    val result3_b = first(
      createFuture(3, "fa", 300, 42),
      createFuture(3, "fb", 200, 43)
    )
    Thread.sleep(1000)
    println(s"#3 result b: $result3_b")

    // 4. last(futurea, futureb) => returns last value of 2 futures to return
    println("\nexercise 4:")
    def last[T](fa: Future[T], fb: Future[T]): Future[T] =
      var lastF: Future[T] = null

      while !fa.isCompleted && !fb.isCompleted do
        Thread.sleep(10)
      
      if fa.isCompleted then fb.map(x => x)
      else fa.map(x => x)
    end last

    val result4_a = last(
      createFuture(3, "fa", 200, 42),
      createFuture(3, "fb", 300, 43)
    )
    Thread.sleep(1000)
    println(s"#4 result a: $result4_a")

    val result4_b = last(
      createFuture(4, "fa", 300, 42),
      createFuture(4, "fb", 200, 43)
    )
    Thread.sleep(1000)
    println(s"#4 result b: $result4_b")

    //  5. retryUntil[[T](action: () => Future[T], condition: T => Boolean): Future[T]
    // println("\nexercise 5:")
    // def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] =
    //   def checkCondition(future: Future[T]): Boolean =
    //     val checkFuture = for
    //       result <- future
    //       // checkPassed <- condition(result)
    //     yield condition(result)
    //     // yield {
    //     //   println(s"checkFuture.value = ${checkFuture.value}")

    //     //   checkFuture.value match
    //     //     case Some(value) => {
    //     //       println(s"Some(value) = $value")
    //     //       value match
    //     //         case Success(value) => value
    //     //         case Failure(exception) => false
    //     //       }
    //     //     case None => {
    //     //       println(s"None: false")
    //     //       false
    //     //     } 
    //     //   }
    //   end checkCondition

    //   def returnFuture(future: Future[T]): Future[T] =
    //     if checkCondition(future) then future.map(x => x)
    //     else {
    //       println(s"condition not met, sleeping...")
    //       Thread.sleep(100)
    //       returnFuture(action())
    //     }

    //   returnFuture(action())
    // end retryUntil
    
    // val action1: () => Future[Int] = () => {
    //   val random = Random()
    //   Thread.sleep(800)
    //   val newVal = random.nextInt(20)
    //   println(s"#5: new value = $newVal")
    //   Future(newVal)
    // }
    // val condition: Int => Boolean = x => x > 15 

    // retryUntil(action1, condition)

  end exercises  
end v30_FuturesAndPromisesExercises

object v30_FuturesAndPromisesExercises_CourseSolution:
  def exercises() =
    println("exercise 1:")
    def fulfillImmediately[T](value: T): Future[T] = Future(value)
    def fulfillImmediatelyAlt[T](value: T): Future[T] = Future.successful(value)
    println(s"#1 fulfillImmediatelyAlt(42): ${fulfillImmediatelyAlt(42)}")


    println("\nexercise 2:")
    def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] =
      first.flatMap(_ => second)


    println("\nexercise 3:")
    def first[A](fa: Future[A], fb: Future[A]): Future[A] =
      val promise = Promise[A]

      // if promise has already been fulfilled, calling success or failure will throw exception
      fa.onComplete(promise.tryComplete)
      fb.onComplete(promise.tryComplete)

      // alternate 1:
      // def tryComplete(promise: Promise[A], result: Try[A]) =
      //   result match            
      //     case Success(value) => try promise.success(value) catch case _ => null
      //     case Failure(ex) => try promise.failure(ex) catch case _ => null
      // fa.onComplete(tryComplete(promise, _))
      // fb.onComplete(tryComplete(promise, _))

      // alternate 2:
      // fa.onComplete {
      //   case Success(value) => try promise.success(value) catch case _ => null
      //   case Failure(ex) => try promise.failure(ex) catch case _ => null
      // }
      // fb.onComplete {
      //   case Success(value) => try promise.success(value) catch case _ => null
      //   case Failure(ex) => try promise.failure(ex) catch case _ => null
      // }      
      
      promise.future
    end first


    println("\nexercise 4:")
    def last[A](fa: Future[A], fb: Future[A]): Future[A] =
      val promiseBoth = Promise[A]
      val promiseLast = Promise[A]

      // if the other promise has fulfilled promiseBoth, then tryComplete for this promise = false
      //    that means this is the last promise, so fulfill the returned promise
      val checkAndComplete: Try[A] => Unit = 
        result => if !promiseBoth.tryComplete(result) then promiseLast.complete(result)

      fa.onComplete(checkAndComplete)
      fb.onComplete(checkAndComplete)

      promiseLast.future
    end last

    val fast = Future {
      Thread.sleep(400)
      42
    }
    val slow = Future {
      Thread.sleep(100)
      43
    }

    first(fast, slow).foreach(first => println(s"FIRST: $first"))
    last(fast, slow).foreach(last => println(s"LAST: $last"))

    Thread.sleep(1000)


    println("\nexercise 5:")
    def retryUntil[A](action: () => Future[A], condition: A => Boolean): Future[A] =
      action()
        .filter(condition)  // will throw 'NoSuchElementException' if filter doesn't pass
        .recoverWith {      // if the filter fails, try it again - if filter was successful, we don't get here 
          case _: Throwable => retryUntil(action, condition)
        }
    end retryUntil

    val random = new Random
    val action = () => Future {
      Thread.sleep(100)
      val nextVal = random.nextInt(100)
      println(s"geerated: $nextVal")
      nextVal
    }
    val conditon = (x: Int) => x < 10
    retryUntil(action, conditon)
      .foreach(result => println(s"settled at: $result"))
    Thread.sleep(5000)

  end exercises
end v30_FuturesAndPromisesExercises_CourseSolution