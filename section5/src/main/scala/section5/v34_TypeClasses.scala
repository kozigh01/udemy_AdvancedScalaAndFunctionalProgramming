package section5

import java.util.Calendar
import javax.swing.text.html.HTML

object v34_TypeClasses {
  def part1() =

    trait HTMLWritable:
      def toHTML: String
    
    case class User(name: String, age: Int, email: String) extends HTMLWritable:
      override def toHTML: String = s"<div>$name ($age yo) <a href=$email /></div>"

    val john = User("John", 32, "john@rockthejvm.com").toHTML
    /*
      Issues with ths approach:
        1. only works for the types WE write
        2. only addresses ONE implementation - may want a "logged-in" and "logged-out" version of User
    */

    // option 2 - pattern matching
    object HTMLSerializerPM:
      def serializeToHTML(value: Any): Unit = value match
        case User(n, a, e) => s"<div>...User...</div>"
        case d: java.util.Date => s"<div>...Date...</div>"
    /*
      1. lost type safety --> using Any
      2. need to modify the code for every element
      3. still only ONE implementation
    */

    // option 3 - better
    trait HTMLSerializer[T]:
      def serialize(value: T): String

    object UserSerializer extends HTMLSerializer[User]:
      def serialize(user: User): String = s"<div>$user</div>"
    object UserSerializerPartial extends HTMLSerializer[User]:
      def serialize(user: User): String = s"<div>$user - Partial</div>"    

    object DateSerializer extends HTMLSerializer[java.util.Date]:
      def serialize(date: java.util.Date) = s"<div>$date</div>"

    val jim: User = User("Jim", 45, "x@x.com")
    println(s"serialize john: ${UserSerializer.serialize(jim)}")
    println(s"serialize john partial: ${UserSerializerPartial.serialize(jim)}")
    println(s"serialize date: ${DateSerializer.serialize(Calendar.getInstance().getTime())}")
    /*
      1. we can define serializers for other types -- such as java.util.Date
      2. can definine multiple serializers for a particular type
    */

    //  TYPE CLASS
    trait MyTypeClassTemplate[T]:
      def action(value: T): String
    // TYPE CLASS INSTANCE
    object MyTypeClassTemplate1 extends MyTypeClassTemplate[Int]:
      def action(value: Int): String = s"the int is: $value"

    /* 
      exeercise:  Equality 
    */
    trait Equal[T]:
      def apply(val1: T, val2: T): Boolean
    object UserEqualityName extends Equal[User]:
      def apply(u1: User, u2: User): Boolean = u1.name == u2.name
    object UserEqualityNameAndEmail extends Equal[User]:
      def apply(u1: User, u2: User): Boolean = u1.name == u2.name && u1.email == u2.email

    val bob = User("Bob", 54, "a@a.com")
    println(s"Equal name for jim and bob: ${UserEqualityName(jim, bob)}")


    // HTMLSerializer - part 2
    object HTMLSerializer:
      def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =   
}
