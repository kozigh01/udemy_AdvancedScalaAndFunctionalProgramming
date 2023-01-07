package section5

object Equality:
  trait HTMLWritable:
    def toHTML: String
  
  case class User(name: String, age: Int, email: String) extends HTMLWritable:
    override def toHTML: String = s"<div>$name ($age yo) <a href=$email /></div>"

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
end Equality