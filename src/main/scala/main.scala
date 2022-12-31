package root

def displaySection(name: String)(f: () => Unit) =
  println(s"---------- $name ----------")
  f() 
  println(s"\n---------- $name ----------")   
def displayPart(file: String, name: String)(f: () => Unit) =
  println(s"\n--     $file - $name: start --\n")  
  f()
  println(s"\n--     $file - $name: end --")  


@main def Main = {
  displaySection("Section 4") {    
    () => {
      // displayPart("Intro to Parellel Programming (video 21)", "Part 1")(section4.v21_IntoParallelProgramming.part1)
      
      // displayPart("Concurrency Problems (video 22)", "Part 1")(section4.v22_ConcurrencyProblems.part1)
      // displayPart("Concurrency Problems (video 22)", "Part 2")(section4.v22_ConcurrencyProblems.part2)
      // displayPart("Concurrency Problems (video 22)", "Excercises")(section4.v22_ConcurrencyProblems.exercises)
      
      // displayPart("JVM Thread Communications (video 23)", "Part1")(section4.v23_ThreadCommunication.part1)
      
      displayPart("Producer-Consumer: Level 2 (video 24)", "Part1")(section4.v24_ProducerConsumer_Level2.part1)
    }
  }
}
