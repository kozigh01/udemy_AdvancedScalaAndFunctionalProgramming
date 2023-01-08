package section5

import java.util.Calendar
import javax.swing.text.html.HTML
import java.util.Date

import Equality.User

object v34_TypeClasses {

  def part1() =
    trait HTMLWritable:
      def toHTML: String
          
    case class User(name: String, age: Int, email: String) extends HTMLWritable:
      override def toHTML: String = s"<div>$name ($age yo) <a href=$email /></div>"
    val john = User("John", 32, "john@rockthejvm.com")
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
      end serializeToHTML
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

    // HTMLSerializer - part 2
    object HTMLSerializer:    // companion object
      def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  
      def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =   
        serializer.serialize(value)

    object IntSerializer extends HTMLSerializer[Int]:
      def serialize(value: Int): String = s"<div>Int serialize - ...$value...</div>"

    println(s"implicit serialize john: ${HTMLSerializer.serialize(42)(IntSerializer)}")

    // when immplementing a trait with one method, can create anonymous implementation with lambda function
    implicit val userHTMLSerializer: HTMLSerializer[User] = (user: User) => s"implicit - ...$user..."
    implicit val dateHTMLSerializer: HTMLSerializer[Date] = (date: Date) => s"implicit - ...$date..."

    println(s"implicit serialize john: ${HTMLSerializer.serialize(jim)}")
    println(s"implicit serialize java.util.Date: ${HTMLSerializer.serialize(Calendar.getInstance().getTime())}")
    
    // alternate that give access to full type class functionality
    println(s"implicit serialize john: ${HTMLSerializer[User].serialize(jim)} ${HTMLSerializer[User].someValue}")

    
    // HTMLSerializer - part 3
    implicit class HTMLEnrichment[T](value: T):
      def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)

    println(s"john.toHTML: ${john.toHTML}")
    println(s"john.toHTML(userSerializer): ${john.toHTML(43)}")

    // context bounds
    def htmlBoilerplate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
      s"<html><body> ${content.toHTML(serializer)} </body></html>"

    // [T : HTMLSerializer] means that there must exist a HTMLSerializer[T], so we can eliminate the implicit parameter
    //    and rely on the HTMLEnrichment implicit class to have a valid HTMLSerializer[T]
    def htmlBoilerplateSugar[T : HTMLSerializer](content: T): String =
      // if the HTMLSerializer is needed, can use implicitly to get it
      val serializer: HTMLSerializer[T] = implicitly[HTMLSerializer[T]]
      // use serializer

      s"<html><body> ${content.toHTML(serializer)} </body></html>"
  }
