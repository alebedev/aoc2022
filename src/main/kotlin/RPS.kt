import java.lang.RuntimeException

// Day 2
fun main() {
    val input = readInput()
    var result = 0
    for (pair in input) {
        val myMove = when(pair.second) {
            Result.Draw -> pair.first
            Result.Win -> when(pair.first) {
                Move.Rock -> Move.Paper
                Move.Paper -> Move.Scissors
                Move.Scissors -> Move.Rock
            }
            Result.Lose -> when(pair.first) {
                Move.Rock -> Move.Scissors
                Move.Paper -> Move.Rock
                Move.Scissors -> Move.Paper
            }
        }
        val game = Pair(pair.first, myMove)
        result += getScore(game)
    }
    println("Total score: $result")
}

private fun readInput(): Iterable<Pair<Move, Result>> {
    val result = mutableListOf<Pair<Move, Result>>()
    val lines = generateSequence(::readLine)
    for (line in lines) {
        val parts = line.split(" ")
        val first = when (parts[0]) {
            "A" -> Move.Rock
            "B" -> Move.Paper
            "C" -> Move.Scissors
            else -> throw RuntimeException("Invalid first char")
        }
        val second = when (parts[1]) {
            "X" -> Result.Lose
            "Y" -> Result.Draw
            "Z" -> Result.Win
            else -> throw RuntimeException("Invalid second char")
        }
        result.add(Pair(first, second))
    }
    return result
}

fun getScore(game: Pair<Move, Move>): Int {
    val my = game.second
    val other = game.first
    var score = when (my) {
        Move.Rock -> 1
        Move.Paper -> 2
        Move.Scissors -> 3
    }
    score += when {
        my == other -> 3
        my == Move.Rock && other == Move.Scissors -> 6
        my == Move.Paper && other == Move.Rock -> 6
        my == Move.Scissors && other == Move.Paper -> 6
        else -> 0
    }
    return score
}

enum class Move {
    Rock,
    Paper,
    Scissors
}

enum class Result {
    Lose,
    Draw,
    Win
}