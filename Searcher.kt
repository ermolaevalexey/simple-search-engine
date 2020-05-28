package search

import java.io.File

class Searcher {
    private val data: MutableList<String> = mutableListOf();
    private val searchIndex = mutableMapOf<String, MutableList<Int>>()
    private var strategy = SearchStrategy.ANY

    constructor(source: File) {
        this.createIndex(source)
    }

    enum class SearchStrategy(val id: String) {
        ALL("ALL"),
        ANY("ANY"),
        NONE("NONE");

        companion object {
            fun getStrategy(str: String): SearchStrategy {
                return valueOf(str)
            }
        }
    }

    enum class SearcherState(val id: String) {
        WAITING("waiting"),
        SEARCHING("searching"),
        STOPPED("default")
    }

    private fun createIndex(source: File) {
        source.forEachLine { line -> data.add(line) }

        for (idx in 0 until data.size) {
            for (word in data[idx].split(' ')) {
                val wordAsKey = word.toLowerCase()
                val record = searchIndex[wordAsKey]
                val positions = mutableListOf<Int>()
                if (record == null) {
                    searchIndex[wordAsKey] = positions
                } else if (data[idx].toLowerCase().contains(wordAsKey)) {
                    searchIndex[wordAsKey]?.add(idx)
                }
                positions.add(idx)
            }
        }
    }

    private var internalState = SearcherState.STOPPED

    val state: SearcherState
        get() = this.internalState

    fun start() {
        internalState = SearcherState.WAITING
    }

    fun stop() {
        internalState = SearcherState.STOPPED
    }

    fun startSearch() {
        internalState = SearcherState.SEARCHING
    }

    fun applyStrategy(str: String) {
        this.strategy = SearchStrategy.getStrategy(str)
    }

    fun search(query: String): MutableList<String> {
        val preparedQuery = query.trim().toLowerCase()

        return when (strategy) {
            SearchStrategy.ANY -> searchWithAnyStrategy(preparedQuery)
            SearchStrategy.ALL -> searchWithAllStrategy(preparedQuery)
            SearchStrategy.NONE -> searchWithNoneStrategy(preparedQuery)
        }
    }

    private fun searchWithAnyStrategy(query: String): MutableList<String> {
        val results = mutableListOf<String>()

        for (word in query.split(' ')) {
            val record = searchIndex[word]

            if (record != null) {
                for (idx in record) {
                    if (data[idx] in results) {
                        continue
                    } else {
                        results.add(data[idx])
                    }
                }
            }
        }

        return results
    }

    private fun searchWithAllStrategy(query: String): MutableList<String> {
        val words = query.split(' ')

        val included = mutableListOf<MutableList<Int>>()
        for (word in words) {
            val record = searchIndex[word]

            if (record != null) {
                included.add(record)
            }
        }

        return if (included.size == words.size) {
            mutableListOf(data[included[0][0]])
        } else {
            mutableListOf()
        }

    }

    private fun searchWithNoneStrategy(query: String): MutableList<String> {
        val words = query.split(' ')
        val results = mutableListOf<String>()
        val ex = mutableListOf<Int>()
        for (word in words) {
            val record = searchIndex[word]
            if (record != null) {
                for (idx in record) {
                    if (idx in ex) {
                        continue
                    } else {
                        ex.add(idx)
                    }
                }
            }
        }

        for (idx in 0 until data.size) {
            if (idx in ex) {
                continue
            } else {
                results.add(data[idx])
            }
        }

        return results
    }


    fun printResults(results: MutableList<String>) {
        for (result in results) {
            println(result)
        }
    }

    fun printMenu() {
        println("""
            === Menu ===
            1. Find a person
            2. Print all people
            0. Exit
        """.trimIndent())
    }

    fun printData() {
        println()
        println("=== List of people ===")
        println(this.getData())
    }

    private fun getData(): String {
        return data.joinToString("\n").trimStart()
    }
}
