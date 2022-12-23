import kotlin.math.abs

fun main() = Seeding.solve()

private object Seeding {
    fun solve() {
        var board = Board(readInput())
        println("Initial")
        board.visualize()
        for (i in 0 until 10) {
            val direction = Direction.values()[i % 4]
            board = board.next(direction)
            println("after turn ${i + 1}")
            board.visualize()
        }
//        board.visualize()
        println("Empty cells after 10 rounds: ${board.emptyCells()}")
    }

    private fun readInput(): Set<Pos> {
        val result = mutableSetOf<Pos>()
        generateSequence(::readLine).forEachIndexed { y, line ->
            line.toCharArray().forEachIndexed { x, char ->
                if (char == '#') result.add(Pos(x, y))
            }
        }
        return result
    }

    class Board(val elves: Set<Pos>) {
        fun visualize() {
            val (xRange, yRange) = getBoundingRect()
            for (y in yRange) {
                for (x in xRange) {
                    print(if (Pos(x, y) in elves) '#' else '.')
                }
                println()
            }
            println()
        }

        fun next(firstLook: Direction): Board {
            val firstLookIndex = Direction.values().indexOf(firstLook)
            val lookDirections = Direction.values().toList().subList(firstLookIndex, Direction.values().size)
                .plus(Direction.values().sliceArray(0 until firstLookIndex))
            require(lookDirections.size == Direction.values().size && lookDirections.first() == firstLook)
            println("Look directions: $lookDirections")

            val moves = mutableMapOf<Pos, Pos>()
            for (elf in elves) {
                var nextPos = elf
                if (elf.neighbours().any { elves.contains(it) }) {
                    for (dir in lookDirections) {
                        if (elf.directionNeighbours(dir.delta.x, dir.delta.y).none { pos -> elves.contains(pos) }) {
                            nextPos = Pos(elf.x + dir.delta.x, elf.y + dir.delta.y)
                            break
                        }
                    }
                    println("Move $elf -> $nextPos")
                }
                moves[elf] = nextPos
            }

            val targets = mutableSetOf<Pos>()
            val conflictingTargets = mutableSetOf<Pos>()
            for (move in moves.values) {
                if (move in targets) {
                    conflictingTargets.add(move)
                } else {
                    targets.add(move)
                }
            }

            val nextElves = moves.map { move ->
                if (move.value in conflictingTargets) {
                    move.key
                } else {
                    move.value
                }
            }.toSet()

            return Board(nextElves)
        }

        fun emptyCells(): Int {
            val (xRange, yRange) = getBoundingRect()
            return (1 + xRange.last - xRange.first) * (1 + yRange.last - yRange.first) - elves.size
        }

        private fun getBoundingRect(): Pair<IntRange, IntRange> {
            val minX = elves.minOf { it.x }
            val maxX = elves.maxOf { it.x }
            val minY = elves.minOf { it.y }
            val maxY = elves.maxOf { it.y }
            return Pair(
                minX..maxX,
                minY..maxY
            )
        }
    }

    enum class Direction(val delta: Pos) {
        North(Pos(0, -1)),
        South(Pos(0, 1)),
        West(Pos(-1, 0)),
        East(Pos(1, 0)),
    }

    data class Pos(val x: Int, val y: Int) {
        fun neighbours(): List<Pos> = listOf(
            Pos(x - 1, y - 1), Pos(x, y - 1), Pos(x + 1, y - 1),
            Pos(x - 1, y), Pos(x + 1, y),
            Pos(x - 1, y + 1), Pos(x, y + 1), Pos(x + 1, y + 1)
        )

        fun directionNeighbours(dx: Int, dy: Int): List<Pos> {
            require(abs(dx) <= 1 && abs(dy) <= 1 && abs(dx) + abs(dy) == 1)
            return if (dx != 0) {
                listOf(Pos(x + dx, y + dx), Pos(x + dx, y), Pos(x + dx, y - dx))
            } else {
                listOf(Pos(x + dy, y + dy), Pos(x, y + dy), Pos(x - dy, y + dy))
            }
        }
    }
}