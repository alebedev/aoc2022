fun main() {
    Crane.solve()
}

object Crane {
    fun solve() {
        val input = readInput()
        println(input)
    }

    private fun readInput(): Input {
        val stacks = mutableListOf<Pair<Int, String>>()
        val moves = mutableListOf<Move>()
        var width = 0
        var isStateSection = true
        for (line in generateSequence(::readLine)) {
            when {
                isStateSection -> {
                    if (line == "") {
                        isStateSection = false
                        continue
                    }
                    val labelsRowMatch = "^\\s*(\\d+\\s*)+$".toRegex().matchEntire(line)
                    if (labelsRowMatch != null) {
                        width = line.trim().split("\\s+".toRegex()).size
                    }
                    val matchResults = "\\[(\\w+)]".toRegex().findAll(line)
                    for (result in matchResults) {
                        val index = result.range.first / 4
                        val value = result.groupValues[1]
                        stacks.add(0, Pair(index, value))
                    }
                }

                else -> {
                    // parse move
                    val matchResult = "^move (\\d+) from (\\d+) to (\\d+)$".toRegex().matchEntire(line)
                        ?: throw Error("Unexpected move input")
                    moves.add(
                            Move(
                                matchResult.groupValues[2].toInt() - 1,
                                matchResult.groupValues[3].toInt() - 1,
                                matchResult.groupValues[1].toInt()
                            )
                        )
                }
            }
        }
        val state = List<MutableList<String>>(width) { mutableListOf() }
        for (item in stacks) {
            state[item.first].add(0, item.second)
        }

        return Input(state, moves)
    }

    private data class Move(val from: Int, val to: Int, val count: Int)

    private data class Input(val state: List<List<String>>, val moves: List<Move>)

}

