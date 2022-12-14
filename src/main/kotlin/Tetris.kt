import kotlin.streams.toList

fun main() = Tetris.solve()

private object Tetris {
    private const val width = 7
    private val winds = mutableListOf<Int>()
    private var fallingRock: FallingRock? = null
    private val cells = mutableListOf(
        // Floor
        MutableList(width) { i -> Cell.StableRock }
    )
    private var rocks = 0
    private var turn = 0
    private var highestRock = 0

    fun solve() {
        winds.clear()
        winds.addAll(readInput())

        val cache = mutableMapOf<Int, Triple<Int, Int, List<Int>>>()
        var cycleSize = Int.MAX_VALUE
        var cycleHeight = 0
        val target = 1_000_000_000_000
        var cycles = 0L
        var currentTarget = target
        while (rocks < currentTarget) {
            if (cycleSize == Int.MAX_VALUE) {
                if (fallingRock == null && rocks > 0 && rocks % 5 == 0) {
//                    println("Rock cycle, round $turn, ${turn % winds.size}")
                    val heights = heights()
                    val minHeight = heights().min()
                    val dHeights = heights.map { it - minHeight }
                    val (cachedRocks, cachedMinHeight, cachedDh) = cache.getOrDefault(
                        turn % winds.size,
                        Triple(0, 0, listOf<Int>())
                    )
                    if (dHeights == cachedDh) {
                        cycleSize = rocks - cachedRocks
                        cycleHeight = minHeight - cachedMinHeight
                        println("Cycle rocks=${cycleSize} height=$cycleHeight $dHeights")
                        currentTarget = rocks + ((target - rocks) % cycleSize)
                        cycles = (target - rocks) / cycleSize
                        println("Setting new target $currentTarget")
                    } else {
                        cache.put(turn % winds.size, Triple(rocks, minHeight, dHeights))
                    }
                    //println("$dHeights ${turn % winds.size}")
                }
            }
            tick()
        }

        println("Total height: shortcut=$highestRock ${highestRock + cycleHeight * cycles}")

    }

    fun heights(): List<Int> {
        return (0 until width).map { x -> (highestRock downTo 0).first { y -> cells[y][x] == Cell.StableRock } }
    }

    private fun tick() {
        addRockOrFall()
        blowAtFallingRock()
        maybeStabilizeFallenRock()
        turn += 1
    }

    private fun addRockOrFall() {
        if (fallingRock == null) {
            val pos = Pos(2, highestRock + 4)
            fallingRock = FallingRock(rockTypes[rocks % rockTypes.size], pos)
            extendCells(fallingRock!!.cells().maxOf { it.y })
        } else {
            moveFalling(0, -1)
        }
    }

    private fun moveFalling(dx: Int, dy: Int): Boolean {
        val initialPos = fallingRock!!.pos
        val nextRock = FallingRock(fallingRock!!.type, Pos(initialPos.x + dx, initialPos.y + dy))
        val moved = !nextRock.cells().any {
            it.x !in 0 until width || cells[it.y][it.x] == Cell.StableRock
        }
        if (moved) {
            fallingRock = nextRock
        }
        return moved
    }

    private fun maybeStabilizeFallenRock() {
        val rockCells = fallingRock!!.cells()
        val isStable = rockCells.any {
            cells[it.y - 1][it.x] == Cell.StableRock
        }
        if (isStable) {
            rockCells.forEach { cells[it.y][it.x] = Cell.StableRock }
            highestRock = maxOf(highestRock, rockCells.maxOf { it.y })
            rocks += 1
            fallingRock = null
        }
    }

    private fun extendCells(maxY: Int) {
        while (maxY >= cells.size) {
            cells.add(MutableList(width) { i -> Cell.Empty })
        }
    }

    private fun blowAtFallingRock() {
        moveFalling(winds[turn % winds.size], 0)
    }

    private fun visualize() {
        println("turn $turn")
        val rockCells = fallingRock?.cells()?.toSet() ?: setOf()
        for (y in cells.indices.reversed()) {
            print('|')
            for (x in cells[y].indices) {
                if (Pos(x, y) in rockCells) {
                    print('@')
                } else if (cells[y][x] == Cell.StableRock) {
                    print(if (y > 0) '#' else '-')
                } else {
                    print('.')
                }
            }
            println('|')
        }
    }

    private fun readInput(): List<Int> = readLine()!!.chars().map {
        when (it) {
            '<'.code -> -1
            '>'.code -> 1
            else -> throw Error("Invalid char $it")
        }
    }.toList()

    private data class Pos(val x: Int, val y: Int)

    private enum class Cell {
        Empty,
        StableRock,
    }

    private enum class RockType(val cells: List<Pos>) {
        HLine(listOf(Pos(0, 0), Pos(1, 0), Pos(2, 0), Pos(3, 0))),
        Cross(listOf(Pos(1, 0), Pos(0, 1), Pos(1, 1), Pos(2, 1), Pos(1, 2))),
        RightAngle(listOf(Pos(0, 0), Pos(1, 0), Pos(2, 0), Pos(2, 1), Pos(2, 2))),
        VLine(listOf(Pos(0, 0), Pos(0, 1), Pos(0, 2), Pos(0, 3))),
        Square(listOf(Pos(0, 0), Pos(1, 0), Pos(0, 1), Pos(1, 1)))
    }


    private data class FallingRock(val type: RockType, val pos: Pos) {
        fun cells() = type.cells.map { Pos(pos.x + it.x, pos.y + it.y) }
    }

    private val rockTypes = listOf(RockType.HLine, RockType.Cross, RockType.RightAngle, RockType.VLine, RockType.Square)
}