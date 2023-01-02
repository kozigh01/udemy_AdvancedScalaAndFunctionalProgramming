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
      
      // displayPart("Producer-Consumer: Level 2 (video 24)", "Part1")(section4.v24_ProducerConsumer_Level2.part1)
      
      // displayPart("Producer-Consumer: Level 3 (video 25)", "Part1 - My Solution")(section4.v25_ProducerConsumer_Level3.part1_MySolution)
      // displayPart("Producer-Consumer: Level 3 (video 25)", "Part2 - Course Solution")(section4.v25_ProducerConsumer_Level3.part2_CourseSolution)
      
      // displayPart("Producer-Consumer: Exercises (video 26)", "Exercise 1")(section4.v26_ThreadCommunications.exercise1_NotifyAll)
      // displayPart("Producer-Consumer: Exercises (video 26)", "Exercise 2")(section4.v26_ThreadCommunications.exercises2_Deadlock)
      // displayPart("Producer-Consumer: Exercises (video 26)", "Exercise 2 Course Solution")(section4.v26_ThreadCommunications.exercises2_Deadlock_CourseSolution)
      // displayPart("Producer-Consumer: Exercises (video 26)", "Exercise 3 Course Solution")(section4.v26_ThreadCommunications.exercises3_Livelock_CourseSolution)
    
      // displayPart("Futures and Promises: FP concurrency (video 27)", "Part 1")(section4.v27_FuturesAndPromises.part1)
      // displayPart("Futures and Promises: FP concurrency 2 (video 28)", "Part 1")(section4.v28_FuturesAndPromises2.part1)
      // displayPart("Futures and Promises: FP concurrency 3 (video 29)", "Part 1")(section4.v29_FuturesAndPromises3.part1)
      // displayPart("Futures and Promises: FP concurrency 3 (video 29)", "Part 2")(section4.v29_FuturesAndPromises3.part2)
      // displayPart("Futures and Promises: Exercises (video 30)", "Exercises")(section4.v30_FuturesAndPromisesExercises.exercises)
      displayPart("Futures and Promises: Exercises (video 30)", "Course Solutions")(section4.v30_FuturesAndPromisesExercises_CourseSolution.exercises)
       
    }
  }
}
