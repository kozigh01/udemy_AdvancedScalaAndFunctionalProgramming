package section6

object v45_Varaince_Exercises:

  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle

  /*
    1. Invariant, covariant, cotravariant

        Parking[T](things List[T]):
          park(vehicle: T)
          impound(vehicles: List[T])
          checkVehicles(conditions: String): List[T]


    3.  Add flatmap
  */
  def exercises() =

    // Invariant
    class ParkingA[T](vehicles: List[T]):
      def park(vehicle: T): ParkingA[T] =
        println(s"parking: $vehicle")
        ParkingA[T](vehicles)
      def impound(vehicles: List[T]): ParkingA[T] =
        print(s"impounding: $vehicles")
        ParkingA[T](vehicles)
      def checkVehicles(donditions: String): List[T] = vehicles

      def flatMap[S](f: T => ParkingA[S]): ParkingA[S] = ???

    // Covariant
    //  seems not well suited since this is a COLLECTION OF ACTIONS
    //    - covariance works better for a COLLECTION OF THINGS
    class ParkingB[+T](vehicles: List[T]):
      def park[B >: T](vehicle: B): ParkingB[B] =
        println(s"parking: $vehicle")
        ParkingB[B](vehicles)
      def impound[B >: T](vehicles: List[B]): ParkingB[B] =
        print(s"impounding: $vehicles")
        ParkingB[B](vehicles)
      def checkVehicles(donditions: String): List[T] = vehicles

      def flatMap[S](f: T => ParkingB[S]): ParkingB[S] = ???

    // Contravariant
    //   seems fitting sense this is a COLLECTION OF ACTIONS
    class ParkingC[-T](vehicles: List[T]):
      def park(vehicle: T): ParkingC[T] =
        println(s"parking: $vehicle")
        ParkingC(vehicles)
      def impound(vehicles: List[T]): ParkingC[T] =   // note: List[T] follows the variance of T, if T is contravariant, then List[T] is also contravariant
        print(s"impounding: $vehicles")
        ParkingC(vehicles)
      def checkVehicles[B <: T](donditions: String): List[B] = List[B]()

      // def flatMap[S](f: T => ParkingC[S]): ParkingC[S] = ???  // contravariant type T occurs in covariant position in type T => ParkingC[S] of parameter f
      def flatMap[B <: T, S](f: B => ParkingC[S]): ParkingC[S] = ???


    /*
      Part 2: using an invariant List 
    */
    class IList[T]

    // Invariant Parking stays the same

    // Covariant
    class ParkingD[+T](vehicles: IList[T]):
      // def park(vehicle: T): ParkingB[T] = ???   // covariant type T occurs in contravariant position in type T of parameter vehicle
      def park[B >: T](vehicle: B): ParkingB[B] = ???

      // def impound(vehicles: IList[T]): ParkingB[T] = ???    // covariant type T occurs in invariant position in type IList[T] of parameter vehicles
      def impound[B >: T](vehicles: IList[B]): ParkingB[B] = ???

      // def checkVehicles(donditions: String): IList[T] = ???  // covariant type T occurs in invariant position in type (donditions: String): IList[T] of method checkVehicles
      def checkVehicles[B >: T](donditions: String): IList[B] = ???  // need hack here also

    // Contravariant
    class ParkingE[-T](vehicles: IList[T]):
      def park(vehicle: T): ParkingC[T] = ???

      // def impound(vehicles: IList[T]): ParkingC[T] = ???    // contravariant type T occurs in invariant position in type IList[T] of parameter vehicles
      def impound[B <: T](vehicles: IList[B]): ParkingC[B] = ???

      // def checkVehicles(donditions: String): IList[T] = ??? // contravarcontravariant type T occurs in invariant position in type (donditions: String): IList[T] of method checkVehiclesbloopiant type T occurs in invariant position in type (donditions: String): IList[T] of method checkVehicles
      def checkVehicles[B <: T](donditions: String): List[B] = ???

    
    
    

    
  @main def main(): Unit =
    exercises()
end v45_Varaince_Exercises