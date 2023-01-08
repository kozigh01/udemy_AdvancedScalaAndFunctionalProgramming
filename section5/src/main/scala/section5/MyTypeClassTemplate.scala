package section5

object MyTypeClassTemplate {
  
    //  TYPE CLASS
    trait MyTypeClassTemplate[T]:
      def action(value: T): String
    object MyTypeClassTemplate:
      def apply[T](implicit instance: MyTypeClassTemplate[T]) = instance

    // TYPE CLASS INSTANCE
    object MyTypeClassTemplate1 extends MyTypeClassTemplate[Int]:
      def action(value: Int): String = s"the int is: $value"
}
