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
      displayPart("Intro to Parellel Programming (video 21)", "Part 1")(section4.v21_IntoParallelProgramming.part1)
      // displayPart("Intro to Parellel Programming (video 22)", "Part 2")(section4.v21_IntoParallelProgramming.part2)
    }
  }
}
