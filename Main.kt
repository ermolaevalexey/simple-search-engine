package search
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val file = File(args[1])
    val scanner = Scanner(System.`in`)
    val searcher = Searcher(file)

    searcher.start()

    while (searcher.state == Searcher.SearcherState.WAITING) {
        println()
        searcher.printMenu()
        val selection = scanner.nextLine()

        when (selection) {
             "0" -> {
                println()
                println("Bye!")
                searcher.stop()
            }
            "1" -> {
                println()
                println("Select a matching strategy: ALL, ANY, NONE")
                val strategy = scanner.nextLine()
                searcher.applyStrategy(strategy)
                println("Enter a name or email to search all suitable people.")
                searcher.startSearch()
                val query = scanner.nextLine()
                val results = searcher.search(query)
                if (results.size > 0) {
                    println("${results.size} persons found:")
                    searcher.printResults(results)
                } else {
                    println("No matching people found.")
                }
                searcher.start()
            }
            "2" -> searcher.printData()
            else -> {
                println("Incorrect option! Try again.")
                searcher.start()
            }
        }
    }
}
