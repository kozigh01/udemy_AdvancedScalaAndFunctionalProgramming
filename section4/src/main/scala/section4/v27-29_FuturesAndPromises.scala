package section4

import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Random}
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Promise

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
  
end v30_FuturesAndPromisesExercises