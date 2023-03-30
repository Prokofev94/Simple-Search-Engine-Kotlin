package search

import java.io.File

lateinit var dataLines: List<String>
val invertedIndex = mutableMapOf<String, MutableList<Int>>()

fun main(args: Array<String>) {
    readData(args[1])
    menu()
    println("Bye!")
}

fun readData(fileName: String) {
    val file = File(fileName)
    dataLines = file.readLines()

    for (i in dataLines.indices) {
        for (word in dataLines[i].split(Regex("\\s+")).map { it.lowercase() }) {
            if (word in invertedIndex) {
                invertedIndex[word]!!.add(i)
            } else {
                invertedIndex[word] = mutableListOf(i)
            }
        }
    }
}

fun menu() {
    while (true) {
        println("=== Menu ===")
        println("1. Find a person")
        println("2. Print all people")
        println("0. Exit")
        val input = readln()
        println()
        when (input) {
            "1" -> findPerson()
            "2" -> printAllPeople()
            "0" -> return
            else -> println("Incorrect option! Try again.")
        }
        println()
    }
}

fun findPerson() {
    println("Select a matching strategy: ALL, ANY, NONE")
    val matchingStrategy = readln()
    println()
    println("Enter a name or email to search all suitable people.")
    val query = readln().lowercase().split(Regex("\\s+"))
    val indices = when (matchingStrategy) {
        "ALL" -> allMatches(query)
        "ANY" -> anyMatches(query)
        else -> noneMatches(query)
    }
    when (indices.size) {
        0 -> println("No matching people found.")
        1 -> println("1 person found:")
        else -> println("${indices.size} persons found:")
    }
    indices.forEach { println(dataLines[it]) }
}

fun printAllPeople() {
    println("=== List of people ===")
    dataLines.forEach { println(it)}
}

fun allMatches(query: List<String>): Set<Int> {
    if (query.first() !in invertedIndex) {
        return emptySet()
    }
    var indices = invertedIndex[query.first()]!!.toList()
    var i = 1
    while (i < query.size && indices.isNotEmpty()) {
        if (query[i] !in invertedIndex) {
            return emptySet()
        }
        indices = indices.filter(invertedIndex[query[i]]!!.toList()::contains)
        i++
    }
    return indices.toSet()
}

fun anyMatches(query: List<String>): Set<Int> {
    val indices = mutableSetOf<Int>()
    for (word in query) {
        if (word in invertedIndex) {
            indices.addAll(invertedIndex[word]!!)
        }
    }
    return indices
}

fun noneMatches(query: List<String>): Set<Int> {
    val indices = dataLines.indices.toMutableSet()
    for (word in query) {
        if (word in invertedIndex) {
            indices.removeAll(invertedIndex[word]!!.toSet())
        }
    }
    return indices
}