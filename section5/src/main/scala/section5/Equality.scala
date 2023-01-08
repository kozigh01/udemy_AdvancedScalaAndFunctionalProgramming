package section5

object Equality extends App:
  case class User(name: String, age: Int, email: String)

  trait Equal[T]:
    def apply(val1: T, val2: T): Boolean
  object Equal:
    def apply[T](implicit instance: Equal[T]) = instance
    def apply[T](a: T, b: T)(implicit instance: Equal[T]) = instance(a, b)

  object UserEqualityName extends Equal[User]:
    def apply(u1: User, u2: User): Boolean = u1.name == u2.name
  object UserEqualityNameAndEmail extends Equal[User]:
    def apply(u1: User, u2: User): Boolean = u1.name == u2.name && u1.email == u2.email

  implicit val UserEqual: Equal[User] = UserEqualityName

  /* 
    exeercise:  Equality 
  */
  val jim: User = User("Jim", 45, "x@x.com")
  val jim2 = User("Jim", 54, "a@a.com")
  println(s"Equal name for jim and jim2: ${UserEqualityName(jim, jim2)}")
  println(s"Equal name for jim and jim2 using companion object: ${Equal[User](jim, jim2)}")
  println(s"Equal name and email for jim and jim2 using companion object: ${Equal(jim, jim2)(UserEqualityNameAndEmail)}")

  implicit class TypesafeEqual[T](value: T):
    def ===(other: T)(implicit equalizer: Equal[T]): Boolean = equalizer(value, other)
    def !==(other: T)(implicit equalizer: Equal[T]): Boolean = !equalizer(value, other)

  println(s"jim === jim2: ${jim === jim2}")
  println(s"jim !== jim2: ${jim !== jim2}")
  /*
    compiler steps for above statements:
      jim === jim2
      jim.===(jim2)   // User doesn't have a method ===, so start looking for implicit conversion
      new TypesafeEqual[User](jim).===(jim2)  // find TypesafeEqual[T] opton for gaining === method
      new TypesafeEqual[User](john).===(jim2)(UserEqual)  // find the valid implicit definition for Equal[T]
  */

  // TYPE SAFE
  // println(s"jim === 42": ${jim === 42}) // compiler throws error - TypesafeEqual demands both operands are same type
end Equality