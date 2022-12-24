fun main() = Blizzards.solve()

private object Blizzards {
    fun solve() {
        var board = readInput()
        board.visualize()
        for (i in 0..10) {
            board = board.next()
            board.visualize()
        }
    }

    private fun readInput(): Board {
        val cells = generateSequence(::readLine).map { line ->
            line.toCharArray().map { char ->
                when (char) {
                    '#' -> Wall
                    '.' -> Space(mutableListOf())
                    '^' -> Space(mutableListOf(Wind.North))
                    '>' -> Space(mutableListOf(Wind.East))
                    '<' -> Space(mutableListOf(Wind.West))
                    'v' -> Space(mutableListOf(Wind.South))
                    else -> throw Error("Failed to parse char $char")
                }
            }
        }.toList()
        return Board(cells)
    }

    data class Board(val cells: List<List<Cell>>) {
        fun visualize() {
            for (line in cells) {
                for (cell in line) {
                    val char = when (cell) {
                        Wall -> '#'
                        is Space -> {
                            if (cell.winds.isEmpty()) {
                                '.'
                            } else if (cell.winds.size == 1) {
                                when (cell.winds.first()) {
                                    Wind.North -> '^'
                                    Wind.East -> '>'
                                    Wind.South -> 'v'
                                    Wind.West -> '<'
                                }
                            } else {
                                cell.winds.size
                            }
                        }
                    }
                    print(char)
                }
                println()
            }
            println()
        }

        fun next(): Board {
            val nextCells = cells.map {
                it.map { cell ->
                    when (cell) {
                        Wall -> Wall
                        is Space -> Space(mutableListOf())
                    }
                }
            }
            cells.forEachIndexed { y, line ->
                line.forEachIndexed { x, cell ->
                    when (cell) {
                        Wall -> {}
                        is Space -> {
                            cell.winds.forEach { wind ->
                                val nextPos = nextWindPos(wind, Pos(x, y))
                                val nextCell = nextCells[nextPos.y][nextPos.x]
                                require(nextCell is Space)
                                nextCell.winds.add(wind)
                            }
                        }
                    }
                }
            }
            return Board(nextCells)
        }

        private fun nextWindPos(wind: Wind, pos: Pos): Pos {
            var nextX = pos.x + wind.dx
            var nextY = pos.y + wind.dy
            // Simplified wrapping, assumes walls are always in outer row
            if (nextX == 0) {
                nextX = width() - 2
            } else if (nextX == width() - 1) {
                nextX = 1
            }
            if (nextY == 0) {
                nextY = height()
            } else if (nextY == height() - 1) {
                nextY = 1
            }
            return Pos(nextX, nextY)
        }

        private fun width(): Int = cells.first().size
        private fun height(): Int = cells.size
    }

    sealed interface Cell

    object Wall : Cell
    data class Space(val winds: MutableList<Wind>) : Cell

    enum class Wind(val dx: Int, val dy: Int) {
        West(-1, 0),
        North(0, -1),
        East(1, 0),
        South(0, 1)
    }

    data class Pos(val x: Int, val y: Int)

    data class Path(val pos: Pos, val target: Pos, val board: Board, val turn: Int) : Comparable<Path> {
        override fun compareTo(other: Path): Int {
            TODO("Not yet implemented")
        }
    }
}