fun main() {
    println(readInput())
}

private fun readInput() {
    var isStateSection = true
    for (line in generateSequence(::readLine)) {
        println(line)
    }
}

private data class Move (val from: Int, val to: Int, val count: Int)

private data class Input (val state: List<String>, val moves: List<Move>)
