package section5

import java.util.Date

object v38_JsonSerialization {
  def part1() =
    println("hello")

    /*
      Users, posts, feeds, etc
      Want to seraialize to Json
    */

    case class User(name: String, age: Int, email: String)
    case class Post(content: String, createdAt: Date)
    case class Feed(user: User, posts: List[Post])

    /*
      want a facility to seriaize these objects
      1. intermediate data types: Int, String, List, Date ...
      2. type classes for conversion to intermediate data types
      3. serialize to Json
    */

    sealed trait JsonValue:  // intermediate data types
      def stringify: String

    final case class JsonString(value: String) extends JsonValue:
      def stringify: String = s"\"$value\""
    final case class JsonNumber(value: Int) extends JsonValue:
      def stringify: String = value.toString()
    final case class JsonDate(value: Date) extends JsonValue:
      def stringify: String = value.toGMTString()
    final case class JsonArray(value: List[JsonValue]) extends JsonValue:
      def stringify: String = value.map(_.stringify).mkString("[", ",", "]")
    final case class JsonObject(values: Map[String, JsonValue]) extends JsonValue:
      /*  For example:
        {
          name: "John"
          age: 22
          friends: [ ... ]
          latestPost: {
            content: "Scala Rocks"
            date: ...
          }
        }
      */
      def stringify: String = values.map {
          case (key, value) => s"\"$key\":${value.stringify}"
        }
        .mkString("{", ",", "}")

    val data = JsonObject(
      Map(
        "user" -> JsonString("Daniel"),
        "posts" -> JsonArray(
          List(
            JsonString("Scala Rocks!"),
            JsonNumber(453)
          ))
      ))
    println(s"data: ${data.stringify}")


    /* 2. type class:
          1. type class
          2. type class instances (implicit)
          3. use type classes: we will use: will use the implicit conversion method
    */
    
    trait JsonConverter[T]:   // 2.1
      def convert(value: T): JsonValue
    object JsonConverter:
      def apply[T : JsonConverter](value: T) = 
        val converter = implicitly[JsonConverter[T]]
        converter.convert(value)

    // 2.2 for existing data types
    implicit object StringConverter extends JsonConverter[String]:  // 2.2
      def convert(value: String): JsonValue = JsonString(value)
    
    implicit object NumberConverter extends JsonConverter[Int]:
      def convert(value: Int): JsonValue = JsonNumber(value)

    // 2.2 for custom types
    implicit object UserConverter extends JsonConverter[User]:
      def convert(user: User): JsonValue = JsonObject(Map(
          "name" -> JsonString(user.name),
          "age" -> JsonNumber(user.age),
          "email" -> JsonString(user.email)
      ))

    implicit object PostConverter extends JsonConverter[Post]:
      def convert(post: Post): JsonValue = JsonObject(Map(
        "content" -> JsonString(post.content),
        "createdAt" -> JsonDate(post.createdAt)
      ))

    implicit object FeedConverter extends JsonConverter[Feed]:
      def convert(feed: Feed): JsonValue = JsonObject(Map(
        "user" -> UserConverter.convert(feed.user),   // TODO refactor
        "posts" -> JsonArray(feed.posts.map(JsonConverter(_)) // a better way
      )))

    

    // call stringify on result
}
