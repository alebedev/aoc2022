import kotlin.math.abs

fun main() = AocMap.solve()

private object AocMap {
    fun solve() {
        val (board, moves) = readInput()
        println("$moves")
//        board.print()
//        val pos = applyMoves(board, moves)
        println("Board size ${board.cells.size}x${board.cells.first().size}")
        println("Cube size ${board.cubeSize()}")
        // Hardcoded for input shape
        val cubeRegions = listOf(
            Pair(0, 1), Pair(0, 2),
            Pair(1, 1),
            Pair(2, 0), Pair(2, 1),
            Pair(3, 0)
        )
//        println("After moves: $pos ${getScore(pos)}")
    }

    private fun applyMoves(board: Board, moves: List<Move>): State {
        var state = board.findInitialState()
        println("Initial state: $state")
        for (move in moves) {
            state = board.applyMove(state, move)
            println("$move $state")
        }
        return state
    }

    private fun getScore(state: State): Int {
        return (state.y + 1) * 1000 + (state.x + 1) * 4 + when (state.facing) {
            Facing.Right -> 0
            Facing.Down -> 1
            Facing.Left -> 2
            Facing.Up -> 3
        }
    }

    fun readInput(): Pair<Board, List<Move>> {
        val rows = mutableListOf<List<Cell>>()
        var inMapSection = true
        var maxWidth = 0
        for (line in generateSequence(::readLine)) {
            if (inMapSection) {
                if (line == "") {
                    inMapSection = false
                    continue
                }
                val cells = line.toCharArray().map {
                    when (it) {
                        ' ' -> Cell.None
                        '.' -> Cell.Empty
                        '#' -> Cell.Wall
                        else -> throw Error("Unexpected char")
                    }
                }.toMutableList()
                maxWidth = maxOf(maxWidth, cells.size)
                if (cells.size < maxWidth) {
                    cells.addAll((1..maxWidth - cells.size).map { Cell.None })
                }
                rows.add(cells)
            } else {
                val moves = "(\\d+)(L|R|$)".toRegex().findAll(line).map { match ->
                    var rotateRight = 0
                    if (match.groupValues[2] == "L") {
                        rotateRight = -1
                    } else if (match.groupValues[2] == "R") {
                        rotateRight = 1
                    }
                    Move(match.groupValues[1].toInt(10), rotateRight)
                }.toList()
                return Pair(Board(rows), moves)
            }
        }

        val moves = listOf<Move>()
        return Pair(Board(rows), moves)
    }

    enum class Facing {
        Right,
        Down,
        Left,
        Up;

        fun rotate(rotateRight: Int): Facing {
            var i = this.ordinal + rotateRight
            if (i == -1) {
                i = Facing.values().size - 1
            } else if (i >= Facing.values().size) {
                i = 0
            }
            return Facing.values()[i]
        }
    }


    data class Move(val len: Int, val rotateRight: Int)

    data class Board(val cells: List<List<Cell>>) {
        fun cubeSize(): Int {
            return cells.first().count { it != Cell.None } / 2
        }

        fun findInitialState(): State {
            return State(cells.first().indexOfFirst { it == Cell.Empty }, 0, Facing.Right)
        }

        fun applyMove(state: State, move: Move): State {
            var x = state.x
            var y = state.y
            when (state.facing) {
                Facing.Right -> x = moveX(state, move.len)
                Facing.Down -> y = moveY(state, move.len)
                Facing.Left -> x = moveX(state, -move.len)
                Facing.Up -> y = moveY(state, -move.len)
            }
            val facing = state.facing.rotate(move.rotateRight)
            return State(x, y, facing)
        }

        private fun moveX(state: State, dx: Int): Int {
            val range = 1..abs(dx)
            val step = dx / abs(dx)
            var x = state.x
            for (i in range) {
                var nextX = x + step
                if (nextX == cells.first().size) {
                    nextX = 0
                } else if (nextX < 0) {
                    nextX = cells.first().size - 1
                }
                while (cells[state.y][nextX] == Cell.None) {
                    nextX += step
                    if (nextX == cells.first().size) {
                        nextX = 0
                    } else if (nextX < 0) {
                        nextX = cells.first().size - 1
                    }
                }
                if (cells[state.y][nextX] == Cell.Wall) {
                    return x
                } else {
                    x = nextX
                }
            }
            return x
        }

        private fun moveY(state: State, dy: Int): Int {
            val range = 1..abs(dy)
            val step = dy / abs(dy)
            var y = state.y
            for (i in range) {
                var next = y + step
                if (next == cells.size) {
                    next = 0
                } else if (next < 0) {
                    next = cells.size - 1
                }
                while (cells[next][state.x] == Cell.None) {
                    next += step
                    if (next == cells.size) {
                        next = 0
                    } else if (next < 0) {
                        next = cells.size - 1
                    }
                }
                if (cells[next][state.x] == Cell.Wall) {
                    return y
                } else {
                    y = next
                }
            }
            return y
        }


        fun print() {
            for (row in cells) {
                println(row.map {
                    when (it) {
                        Cell.None -> ' '
                        Cell.Empty -> '.'
                        Cell.Wall -> '#'
                    }
                }.joinToString(""))
            }
        }


    }

    enum class Cell {
        None,
        Empty,
        Wall
    }


    data class State(val x: Int, val y: Int, val facing: Facing)
}