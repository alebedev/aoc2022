import java.util.*
import kotlin.math.abs

fun main() = Blizzards.solve()

private object Blizzards {
    fun solve() {
        var board = readInput()

        val start = Pos(1, 0)
        val target = Pos(board.cells.last().indexOfFirst { it is Space }, board.cells.size - 1)
        val initialPath = Path(
            start,
            target,
            0
        )
        val bestFwd = findPath(initialPath, board)
        println("Best path S->F takes $bestFwd steps")
        (1..bestFwd).forEach { board = board.next() }
        val backPath = Path(target, start, 0)
        val bestBack = findPath(backPath, board)
        println("Best path F->S takes $bestBack steps")
        (1..bestBack).forEach { board = board.next() }
        val bestBackAgain = findPath(initialPath, board)
        println("Best path S->F (again) takes $bestBackAgain steps")
        println("Total: ${bestFwd + bestBack + bestBackAgain}")
    }

    private fun findPath(initialPath: Path, initialBoard: Board): Int {
        val boards = mutableListOf(initialBoard)
        val queue = PriorityQueue<Path>()
        val visited = mutableSetOf<Path>()
        queue.add(initialPath)
        while (queue.isNotEmpty()) {
            val path = queue.remove()
            if (path.pos == path.target) {
                return path.turn
            }

            if (path in visited) continue
            else visited.add(path)

            val nextTurn = path.turn + 1
//            println("$nextTurn $path")
            if (boards.size == nextTurn) {
                boards.add(boards.last().next())
            }
            val nextBoard = boards[nextTurn]
            val nextPositions = listOf(
                path.pos,
                Pos(path.pos.x - 1, path.pos.y), Pos(path.pos.x + 1, path.pos.y),
                Pos(path.pos.x, path.pos.y - 1), Pos(path.pos.x, path.pos.y + 1)
            ).filter { pos ->
                (nextBoard.cells.getOrNull(pos.y)?.getOrNull(pos.x) as? Space)?.winds?.isEmpty() ?: false
            }
            nextPositions.forEach { pos ->
                queue.add(Path(pos, path.target, nextTurn))
            }
        }
        return -1
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
            } else if (nextX >= width() - 1) {
                nextX = 1
            }
            if (nextY == 0) {
                nextY = height() - 2
            } else if (nextY >= height() - 1) {
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

    data class Path(val pos: Pos, val target: Pos, val turn: Int) : Comparable<Path> {
        override fun compareTo(other: Path): Int = minTurnsToTarget().compareTo(other.minTurnsToTarget())

        private fun minTurnsToTarget() = abs(target.x - pos.x) + abs(target.y - pos.y) + turn
    }
}