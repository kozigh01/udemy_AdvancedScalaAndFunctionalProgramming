package section5

import java.util.Calendar
import javax.swing.text.html.HTML
import java.util.Date


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
      val someValue = "I am a value"

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
    object MyTypeClassTemplate:
      def apply[T](implicit instance: MyTypeClassTemplate[T]) = instance

    // TYPE CLASS INSTANCE
    object MyTypeClassTemplate1 extends MyTypeClassTemplate[Int]:
      def action(value: Int): String = s"the int is: $value"

    /* 
      exeercise:  Equality 
    */
    import Equality.*

    val jim2 = User("Jim", 54, "a@a.com")
    println(s"Equal name for jim and jim2: ${UserEqualityName(jim, jim2)}")
    println(s"Equal name for jim and jim2 using companion object: ${Equal[User](jim, jim2)}")
    println(s"Equal name and email for jim and jim2 using companion object: ${Equal(jim, jim2)(UserEqualityNameAndEmail)}")


    // HTMLSerializer - part 2
    object HTMLSerializer:    // companion object
      def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  
      def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =   
        serializer.serialize(value)


    object IntSerializer extends HTMLSerializer[Int]:
      def serialize(value: Int): String = s"<div>Int serialize - ...$value...</div>"

    println(s"implicit serialize john: ${HTMLSerializer.serialize(42)(IntSerializer)}")

    implicit val userHTMLSerializer: HTMLSerializer[User] = (user: User) => s"implicit - ...$user..."
    implicit val dateHTMLSerializer: HTMLSerializer[Date] = (date: Date) => s"implicit - ...$date..."

    println(s"implicit serialize john: ${HTMLSerializer.serialize(jim)}")
    println(s"implicit serialize java.util.Date: ${HTMLSerializer.serialize(Calendar.getInstance().getTime())}")
    
    // alternate that give access to full type class functionality
    println(s"implicit serialize john: ${HTMLSerializer[User].serialize(jim)} ${HTMLSerializer[User].someValue}")
}
